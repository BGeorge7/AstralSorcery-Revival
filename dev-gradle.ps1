param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]] $GradleArgs = @("tasks")
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$jdkHome = Resolve-Path (Join-Path $projectRoot "..\work\jdk21\jdk-21.0.11+10")

$env:JAVA_HOME = $jdkHome.Path
$env:Path = "$($env:JAVA_HOME)\bin;$env:Path"

& (Join-Path $projectRoot "gradlew.bat") --no-daemon @GradleArgs
