param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]] $GradleArgs = @("tasks")
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$preferredJdk = "C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot"

if (Test-Path $preferredJdk) {
    $jdkHome = Resolve-Path $preferredJdk
} elseif ($env:JAVA_HOME -and (Test-Path $env:JAVA_HOME)) {
    $jdkHome = Resolve-Path $env:JAVA_HOME
} else {
    throw "JDK 21 was not found. Install Eclipse Temurin JDK 21 and set JAVA_HOME."
}

$env:JAVA_HOME = $jdkHome.Path
$env:Path = "$($env:JAVA_HOME)\bin;$env:Path"

& (Join-Path $projectRoot "gradlew.bat") --no-daemon @GradleArgs
