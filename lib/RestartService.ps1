[cmdletBinding()]
param(

[string]
$File = "serverlist.csv"
)

$Stamp = Get-Date -Format "yyyy/MM/dd HH:mm:ss zzz"

$Servers = @()
$Source = Import-Csv -Path $File

ForEach($Line in $Source){
    
    $Target = $Line.Server
    If($Service = Get-Service "Logscape Agent" -ComputerName $Target -ErrorAction SilentlyContinue){
        Write-Output "$Stamp, $Target Level:INFO Service:RESTARTING"
        Restart-Service $Service
        $Servers += $Line.Server
    }
    Else{
        Write-Warning "$Stamp, $Target Level:WARN Service:NOTFOUND"
    }
}

Write-Output "$Stamp, $Servers Level:INFO Services Restarted"
