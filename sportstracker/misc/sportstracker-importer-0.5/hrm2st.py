#!/usr/bin/env python

import sys
import os
import re
import logging
import subprocess
import time

from cStringIO import StringIO
from optparse import OptionParser
from xml.etree.ElementTree import Element, ElementTree


#
# 
# global variables
#
prg       = None
opts      = None



def parseHRM(parser, hrm_file):

    global prg

    out = {}

    cmd = [parser, '--sportstracker', hrm_file]

    DEVNULL = os.open(os.devnull, os.O_RDWR)

    try:
        proc = subprocess.Popen( cmd, stdout=subprocess.PIPE, stderr=DEVNULL )
        parser_output = proc.stdout.read()
        ret = proc.wait()
        if ret:
            raise

    except KeyboardInterrupt:
        sys.stderr.write("%s: User-generated interruption, quitting\n" % prg)
        sys.exit(1)

    except:
        sys.stderr.write("%s: WARNING: unable to parse the '%s' file, skipping\n" % (prg, hrm_file))
        return []

    logging.debug('HRM: parser: \n\n%s' % parser_output)

    hrm = parser_output.split('\n')
    for pair in hrm:
        if pair:
            key, value = pair.split(':', 1)
            out[key] = value

    out['intensity']     = 'NORMAL'


    logging.debug('HRM: parser: %s' % `out`)

    return out



def parseXML(xml):

    global opts
    global prg

    max       = 0
    known_hrm = {}

    logging.debug('searching for last ID...')
    if opts.verbose:
        sys.stderr.write("%s: parsing XML...\n" % (prg, ))

    
    for element in xml.getroot().getchildren():
        if element.tag == 'exercise':
            for sub_element in element.getchildren():
                if sub_element.tag == 'hrm-file':
                    known_hrm[sub_element.text] = True
                if sub_element.tag == 'id':
                    try:
                        id = int(sub_element.text)
                        if id > max:
                            max = id
                    except:
                        pass


    logging.debug('last ID %s' % max)

    return max, known_hrm




def initXML(xml_output):

    f = StringIO('<?xml version="1.0" encoding="UTF-8"?><exercise-list></exercise-list>')
    xml = ElementTree(file=f)

    return xml



def main():

    global prg
    global opts

    #
    # default variables
    # 
    prg        = os.path.basename( sys.argv[0] )
    usage      = prg  + ' [ <OPTIONS> ] FILE1.HRM [ FILE2.HRM [ ... ] ]'

    PATH_TO_CURRENT_SCRIPT = os.path.dirname(os.path.abspath(sys.argv[0]))
    HRM_PARSR = os.path.join(PATH_TO_CURRENT_SCRIPT, 'hrmparser.py')
 
    #
    # parse command line, result is stored to 'opts'
    #
    parser = OptionParser( usage, version="%s version 0.1" % prg )
        
    parser.add_option( '-v', '--verbose', action='store_true', dest='verbose',
                       default=False, help='be verbose' )
    parser.add_option( '-f', '--force', action='store_true', dest='force',
                       default=False, help='re-create output file' )
    parser.add_option( '-a', '--append', action='store_true', dest='append',
                       default=False, help='append new data' )
    parser.add_option( '--sportType', action='store', dest='sportType',
                       default='1', help='use SPORTTYPE as sport type id; default 1' )
    parser.add_option( '--sportSubType', action='store', dest='sportSubType',
                       default='1', help='use SPORTSUBTYPE as sport subtype id; default 1' )
    parser.add_option( '-D', '--debug', action='store_true', dest='debug',
                       default=False, help='enter in debugging mode' )
    parser.add_option( '-o', '--output', action='store', dest='output',
                       default='exercises.xml', help='XML output name; default exercises.xml' )
   


    opts, args = parser.parse_args()

    #   
    # debuging
    #   
    if opts.debug:
        d_level = logging.DEBUG
    else:   
        d_level = None

    logging.basicConfig(level=d_level,
                        # filename='debug_%s' % prg,
                        format='%(asctime)s %(levelname)s %(funcName)s(): %(message)s',
                        filemode='a')

    logging.debug('START')

    if not args:
        sys.stderr.write("%s: no .HRM file given\n" % prg)
        sys.exit(1)


    xml_output = opts.output

    if os.path.isfile(xml_output):
        if not opts.force and not opts.append:
            sys.stderr.write("%s: output %s already exists\n" % (prg, xml_output))
            sys.exit(1)

    index     = 0
    known_hrm = {}

    if opts.append and os.path.isfile(xml_output):
        if 1:
            xml              = ElementTree(file=xml_output)
            index, known_hrm = parseXML(xml)
        else:
            sys.stderr.write("%s: the file '%s' is corrupted, quitting\n" % (prg, xml_output))
            sys.exit(1)
            

    else:
        xml = initXML(xml_output)

    for hrm_file in args:

        full_hrm_file = os.path.abspath(hrm_file)

        if known_hrm.has_key(full_hrm_file):
            sys.stderr.write("%s: HRM file '%s' already present, skipping\n" % (prg, hrm_file))
            continue

        index += 1
        logging.debug('staring with: %s' % hrm_file)
        if opts.verbose:
            sys.stderr.write("%s: procesing file '%s'\n" % (prg, hrm_file))

        values = parseHRM(HRM_PARSR, hrm_file)

        if not values:
            continue

        el_exercise = Element('exercise')

        el      = Element('id')
        el.text = '%d' % index
        el_exercise.append(el)

        el      = Element('sport-type-id')
        el.text = opts.sportType
        el_exercise.append(el)

        el      = Element('sport-subtype-id')
        el.text = opts.sportSubType
        el_exercise.append(el)

        el      = Element('date')
        el.text = values['date']
        el_exercise.append(el)

        el      = Element('duration')
        el.text = values['duration']
        el_exercise.append(el)

        el      = Element('intensity')
        el.text = values['intensity']
        el_exercise.append(el)

        el      = Element('distance')
        el.text = values['distance']
        el_exercise.append(el)


        #compute avg speed
        avg_speed = 0
        try:
            avg_speed = float(values['distance'])/(float(values['duration'])/60.0/60.0)
        except:
            pass

        el      = Element('avg-speed')
        el.text = str(avg_speed)
        el_exercise.append(el)

        el      = Element('avg-heartrate')
        el.text = values['avg-heartrate']
        el_exercise.append(el)

        el      = Element('hrm-file')
        el.text = full_hrm_file
        el_exercise.append(el)

        xml.getroot().append(el_exercise)
        known_hrm[full_hrm_file] = True

        logging.debug('end with: %s' % hrm_file)
            
    xml.write(xml_output)
   
    # format XML soubor: split each attribute to new line
    txt = file(xml_output).read()
    file(xml_output,'w').write(re.sub('><', '>\n<', txt)+'\n')



if __name__ == '__main__':
    main()

