; SportsTracker Inno Setup File

#define MyAppName "SportsTracker"
#define MyAppVersion "7.6.0"
#define MyAppPublisher "Stefan Saring"
#define MyAppURL "https://saring.de/sportstracker/"
#define MyAppExeName "SportsTracker.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{70E513C4-8AAF-4CA7-BAE5-D664666F5285}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppVerName={#MyAppName}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf64}\{#MyAppName}
DisableProgramGroupPage=yes
DisableDirPage=no
OutputBaseFilename={#MyAppName}-{#MyAppVersion}
Compression=lzma
SolidCompression=yes
ChangesAssociations=yes
OutputDir=package\InnoSetup

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "package\SportsTracker\SportsTracker.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "package\SportsTracker\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{commonprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

[Registry]
