;This file will be executed next to the application bundle image
;I.e. current directory will contain folder ZECmate with application files
[Setup]
AppId={{fxApplication}}
AppName=ZECmate
AppVersion=1.1.0
AppVerName=ZECmate 1.1.0
AppPublisher=Zel Technologies GmbH
AppComments=ZECmate
AppCopyright=Copyright (C) 2018
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={pf}\ZECmate
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=No
DisableFinishedPage=No
DisableWelcomePage=Yes
DefaultGroupName=Zel Technologies GmbH
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=ZECmate-1.1.0
Compression=lzma
SolidCompression=yes
PrivilegesRequired=poweruser
SetupIconFile=ZECmate\ZECmate.ico
UninstallDisplayIcon={app}\ZECmate.ico
UninstallDisplayName=ZECmate
WizardImageStretch=No
WizardSmallImageFile=ZECmate-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64
VersionInfoVersion=1.1.0


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "ZECmate\ZECmate.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "ZECmate\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\ZECmate"; Filename: "{app}\ZECmate.exe"; IconFilename: "{app}\ZECmate.ico"; Check: returnTrue()
Name: "{commondesktop}\ZECmate"; Filename: "{app}\ZECmate.exe";  IconFilename: "{app}\ZECmate.ico"; Check: returnFalse()


[Run]
Filename: "{app}\ZECmate.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\ZECmate.exe"; Description: "{cm:LaunchProgram,ZECmate}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\ZECmate.exe"; Parameters: "-install -svcName ""ZECmate"" -svcDesc ""ZECmate"" -mainExe ""ZECmate.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\ZECmate.exe "; Parameters: "-uninstall -svcName ZECmate -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
