#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-
##############################################################################
#
# This module does a first parse of the POLAR HRM file format (heart
# rate monitor). If get all the major information from file, but I 
# have to made a major rewrite in order to get lap times, etc. This
# file is generated with HRM 625sx, 725sx, 810 models.
#
# Juan M. Casillas <juanm.casillas@jmcresearch.com>
# 30/03/2007 21:13:00
#
# Jiri Polcar <polcar@physics.muini.cz>
# 10/03/2009  fix some minor bugs, extend for version 107, add commandline interface
#
# $Id$
#
# $Log$
#
##############################################################################

import os
import sys
import re
import time
import calendar
import datetime
import logging

import matplotlib.pyplot as plt


from math import ceil
from optparse import OptionParser


class HRMParser:
    def __init__(self, fname=None):
        self.fname = fname

    def Parse(self):
        #
        # [section]
        # item
        # item
        # ...
        # <empty>
        # [section2] 
        
        sections = {}
        
        try:
            logging.info('going to open ' + self.fname + ' file')
            fd = file(self.fname)
        except:
           logging.error('unable to open: ' + self.fname)
           return None


        section_r = re.compile("\[(.+)\]")
        
        i = fd.readline()
        while i:
            matches = section_r.findall(i)
            
            if matches:
                logging.debug("match: " + ', '.join(matches))
                attrs = {}
                data  = []
                    
                # a section has been found
                # if items are in the form attribute=value,
                # store them in a dictionary, else, create
                # a funky tuple and save it under data
                
                i = fd.readline()
                while i:
                    i = i.strip()
                    v = i.split("=")
                    
                    if i == '':
                        break
                    
                    if len(v)>1: 
                        attrs[v[0]] = v[1]
                    else:
                        v = re.split("\s+",i)
                        data.append( tuple(v) )

                    i = fd.readline()
                
                sections[matches[0]] = { 'attrs': attrs, 'data': data }
                
            i = fd.readline()    
        
        fd.close()
        return sections


class PolarClass:
    MONITOR_TYPE = [ 
        "NO_MONITOR",
        "Polar Sport Tester / Vantage XL",
        "Polar Vantage NV (VNV)",
        "Polar Accurex Plus",
        "Polar XTrainer Plus",
        "NONE_5",
        "Polar S520",
        "Polar Coach",
        "Polar S210",
        "Polar S410",
        "Polar S510",
        "Polar S610 / S610i",
        "Polar S710 / S710i / S720i",
        "Polar S810 / S810i",
        "Polar E600",
        "NONE_15",
        "NONE_16",
        "NONE_17",
        "NONE_18",
        "Polar AXN500",
        "Polar AXN700",
        "Polar S625X / S725X",
        "Polar S725",
        "NONE_23",
        "NONE_24",
        "NONE_25",
        "NONE_26",
        "NONE_27",
        "NONE_28",
        "NONE_29",
        "NONE_30",
        "NONE_31",
        "NONE_32",
        "NONE_33",
        "NONE_34",
        "NONE_35",
        "Polar RS400",
        ]

    def __init__(self):
        # params
        self.version = None
        self.monitor = PolarClass.MONITOR_TYPE[0]
        self.speed    = False
        self.cadence  = False
        self.altitude = False
        self.power    = False
        self.powerRL  = False
        self.powerPEL = False
        self.hrcc     = False
        self.units    = 0       # 0: Euro (km, km/h, m, C) , 1: US 
        self.airpress = False
        self.date     = None
        self.start_time  = 0
        self.end_time    = 0
        self.length_time = 0
        self.interval  = 5      # seconds between capture
        self.limits = [ (0, 0), (0, 0), (0, 0) ] # three limits
        self.timers = [ 0, 0, 0 ]                  # three timers
        self.active_limit  = 0
        self.maxhr         = 0
        self.resthr        = 0
        self.vo2max        = 0
        self.weight        = 0
        self.in_zone       = [ 0 for x in range(0,10) ]
        
        # hr zones
        
        self.hrzones = [ (0, 0) ] * 10
        
        # trip information

        self.distance      = 0
        self.ascent        = 0
        self.trip_duration = 0
        self.avg_altitude  = 0
        self.max_altitude  = 0
        self.avg_speed     = 0
        self.max_speed     = 0
        self.odometer      = 0
        
        self.hrdata = []


    def LoadFromFile(self, fname):
        parser = HRMParser(fname)
        data = parser.Parse()

        if not data:
            logging.error('data not loaded, EXIT')
            sys.exit(1)
        
        #
        # set default values for the polar using the pdf as reference.
        #
        
        p = data['Params']['attrs']
        
        self.version = p['Version']
        self.monitor = PolarClass.MONITOR_TYPE[int(p['Monitor'])]
    

        flags = [int(x) for x in list(p['SMode'])]
        logging.debug('flags: ' + p['SMode'])
        if self.version == "106":
            if flags[0]: self.speed    = True
            if flags[1]: self.cadence  = True
            if flags[2]: self.altitude = True
            if flags[3]: self.power    = True
            if flags[4]: self.powerRL  = True
            if flags[5]: self.powerPEL = True
            if flags[6]: self.hrcc     = True
            if flags[7]: self.units    = 1         # US        
        if self.version == "107":
            if flags[8]: self.airpress = True


        # start time
        
        year = p['Date'][0:4]
        month= p['Date'][4:6]
        day  = p['Date'][6:8]
        
        # TODO: hack
        hour = "%02d" % (int( p['StartTime'][0:2] ) - 0,)
        min  = p['StartTime'][3:5]
        sec  = int(ceil(float(p['StartTime'][7:])))
        
        datetime = "%s-%s-%s %s:%s:%02d" % (day,month,year,hour,min,sec)
              
        self.start_time = \
             calendar.timegm(time.strptime(datetime,"%d-%m-%Y %H:%M:%S"))
        
        # length time (duration)

        hour = int(p['Length'][0:2])
        min  = int(p['Length'][3:5])
        sec  = int(ceil(float(p['Length'][7:])))
                     
        self.length_time = (hour * 3600) + (min * 60) + sec
   
        # end time

        self.end_time = self.start_time + self.length_time

        # interval
     
        self.interval = int(p['Interval'])
        
        # limits and timers
        
        for i in range(1,4):
            self.limits[i-1] = (int(p['Lower%d' % i]),int(p['Upper%d' % i]))
        
        for i in range(1,4):
            hour = int(p['Timer%d' % i][0:2])
            min  = int(p['Timer%d' % i][3:5])
            sec  = int(ceil(float(p['Timer%d' % i][7:])))
                     
            self.timers[i-1] = (hour * 3600) + (min * 60) + sec      

   
        # active limits, hr and so on
        
        self.active_limit  = int(p['ActiveLimit'])
        self.maxhr         = int(p['MaxHR'])
        self.resthr        = int(p['RestHR'])
        self.vo2max        = int(p['VO2max'])
        self.weight        = int(p['Weight'])
        
        #
        # HR Zones
        #
        
        zones = data['HRZones']['data']
        for i in range(0,10):
            self.hrzones[i] = ( int(zones[i+1][0]), int(zones[i][0]) )
    
        #
        # trip information
        #
        
        try:
            trip = data['Trip']['data']
        
            d = trip[0][0]
            self.distance      = float("%s.%s" % (d[0:-1],d[-1:]))
            self.ascent        = int(trip[1][0])
            self.trip_duration = int(trip[2][0])
            self.avg_altitude  = int(trip[3][0])
            self.max_altitude  = int(trip[4][0])
        
            d = trip[5][0]
            self.avg_speed     = float("%s.%s" % (d[0:-1],d[-1:]))
        
            d = trip[6][0]
            self.max_speed     = float("%s.%s" % (d[0:-1],d[-1:]))
            self.odometer      = float(trip[7][0]) 
        except:
            logging.info('no trip data available')


        #
        # hr data
        #
        
        data = data['HRData']['data']
        stamp = self.start_time

        for i in data:
            hr       = int(i[0])
            speed    = None
            altitude = None

            if self.speed:    
                d = i[1]
                speed = float("%s.%s" % (d[0:-1],d[-1:]))

            if self.altitude: 
                logging.debug(self.altitude)
                altitude = i[2]
            
            self.hrdata.append( (stamp, hr, speed, altitude) )  
            
            # count hr in cones
            for i in range(0,10):
                if hr >=self.hrzones[i][0] and  hr <= self.hrzones[i][1]:
                    self.in_zone[i] += 1

            stamp = stamp + self.interval
            
        
        
if __name__ == "__main__":

    version = '0.1'

    logging.basicConfig(level=None, # ogging.DEBUG,
                        format='%(asctime)s %(levelname)s %(message)s',
                        filemode='w')


    prg        = os.path.basename( sys.argv[0] )
    usage      = prg  + ' [ <OPTIONS> ] <ARGS>'

    parser = OptionParser( usage, version="%s version %s" % ( version, prg) )


    #
    # command-line options
    #

    parser.add_option( '-v', '--verbose', action='store_true', dest="verbose",
                       default=False, help='be verbose' )

    parser.add_option( '-s', '--show', action='store_true', dest="show",
                       default=False, help='show statistics' )

    parser.add_option( '-p', '--plot', action='store_true', dest="plot",
                       default=False, help='show plot' )

    parser.add_option( '-t', '--sportstracker', action='store_true', dest="sportstracker",
                       default=False, help='output for SportsTracker' )

    opts, args = parser.parse_args(sys.argv[1:])



    for filename in args:
        hrm = PolarClass()
        hrm.LoadFromFile(filename)


        if hrm.units:
            UNITS = ( 'miles', 'mph',  'ft', 'F', 'lb' )
        else:
            UNITS = ( 'km',    'km/h', 'm',  'C', 'kg' )


        if opts.verbose:
            print "\n>>>> Device info <<<<\n"

        if opts.verbose:
            sys.stdout.write("Version: ")
        if opts.show:
            print hrm.version

        if opts.verbose:
            sys.stdout.write("Device: ")
        if opts.show:
            print hrm.monitor


        if opts.verbose:
            sys.stdout.write("Interval(s): ")
        if opts.show:
            print hrm.interval

        if opts.verbose:
            print "\n>>>> Personal info <<<<\n"

        if opts.verbose:
            sys.stdout.write("Limits: ")
        if opts.show:
            print hrm.limits

        if opts.verbose:
            sys.stdout.write("Weight: ")
        if opts.show:
            print hrm.weight, UNITS[4]

        if opts.verbose:
            sys.stdout.write("Max. HR: ")
        if opts.show:
            print hrm.maxhr

        if opts.verbose:
            sys.stdout.write("Rest HR: ")
        if opts.show:
            print hrm.resthr

        if opts.verbose:
            sys.stdout.write("VO2max: ")
        if opts.show:
            print hrm.vo2max

        if opts.verbose:
            print "\n>>>> Data info <<<<\n"

        if opts.verbose:
            present = []
            if hrm.speed: present.append('speed')
            if hrm.cadence: present.append('cadence')
            if hrm.altitude: present.append('altitude')
            if hrm.power: present.append('power')
            if hrm.powerRL: present.append('powerRL')
            if hrm.powerPEL: present.append('powerPEL')
            if hrm.hrcc == 0: present.append('heart rate')
            if hrm.hrcc == 1:
                present.append('cycling data')
                present.append('heart rate')
            print "Data present: " + ', '.join(present)


        if opts.verbose:
            sys.stdout.write("Odometer: ")
        if opts.show:
            print hrm.odometer

        if opts.verbose:
            sys.stdout.write("Max. speed: ")
        if opts.show:
            print hrm.max_speed, UNITS[1]

        if opts.verbose:
            sys.stdout.write("Avg. speed: ")
        if opts.show:
            print hrm.avg_speed, UNITS[1]
        if opts.sportstracker:
            print "avg-speed:%.1f" % hrm.avg_speed

        if opts.verbose:
            sys.stdout.write("Distance: ")
        if opts.show:
            print hrm.distance, UNITS[0]
        if opts.sportstracker:
            print "distance:%d" % hrm.distance


        if opts.verbose:
            sys.stdout.write("Ascent: ")
        if opts.show:
            print hrm.ascent, UNITS[2]

        if opts.verbose:
            sys.stdout.write("Start time(%d): " % hrm.start_time)
        if opts.show:
            print time.ctime(hrm.start_time)
        if opts.sportstracker:
            print  "date:%s" % time.strftime('%Y-%m-%dT%H:%M:%S', time.gmtime(hrm.start_time))

        if opts.verbose:
            sys.stdout.write("End time(%d):   " % hrm.end_time)
        if opts.show:
            print  time.ctime(hrm.end_time)

        if opts.verbose:
            sys.stdout.write("Duration: ")

        sec = hrm.trip_duration
        hour = int(sec/60.0/60.0)
        min  = int((sec-hour*60*60)/60.0)
        sec  = sec - hour*60*60 - min*60
        if opts.show:
            print "%d:%02d:%02d" % ( hour, min, sec )
        if opts.sportstracker:
            print "duration:%d" % hrm.trip_duration

        if opts.verbose:
            sys.stdout.write("Avg. altitude: ")
        if opts.show:
            print hrm.avg_altitude, UNITS[2]

        if opts.verbose:
            sys.stdout.write("Max. altitude: ")
        if opts.show:
            print hrm.max_altitude, UNITS[2]

        # show time in zones
        i = 0
        total = 0
        for x in hrm.in_zone:
            total += x
        for (z_from, z_to) in hrm.hrzones:
            if z_to:
                try:
                    frac = 100.0*hrm.in_zone[i]/total
                except:
                    frac = 0.0
                if opts.show:
                    print "Zone %03d .. %03d: %5.2f %%" % ( z_from, z_to, frac )
            i += 1

        sum = 0
        for beat in hrm.hrdata:
            sum += beat[1]
        if opts.sportstracker:
            print "avg-heartrate:%d" % int(sum/len(hrm.hrdata))



        if opts.plot:
            x_time = []
            y_hr   = []
            first  = 0
            for (stamp, hr, speed, altitude) in hrm.hrdata:
                x_time.append(stamp-hrm.start_time)
                y_hr.append(hr)

                if not first:
                    first = stamp


            plt.plot(x_time,y_hr)

            i = 0
            title = [ 'Maximum intensity', 'Hard intensity','Moderate intensity',
                      'Light intensity', 'Very light intensity' ]
            for (z_from, z_to) in hrm.hrzones:
                i += 1
                if z_from:
                    plt.axhline(y=z_from,linewidth=1, color='r')
                    plt.axhspan(z_from, z_to, facecolor='g', alpha=1.0/i)
                    plt.annotate(title[i-1], xy=(first, (z_from+z_to)/2.0),  xycoords='data')


            plt.show()

