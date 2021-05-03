# BEDCS
a prototype blockchain-enabled data credibility system for use in autonomous vehicles
How to use:
first generate a key pair and license by acting as the trusted authority. an example should look something like this, change the "RSU" on line 17 of GenKeys.java to "VEHICLE" if generating a license for a vehicle:

Vehicle1:
------------------
Private Key: MD4CAQAwEAYHKoZIzj0CAQYFK4EEAAoEJzAlAgEBBCBTN10MDCq/UShGO6KZeoeRkFqC+4iAcw7ONDhS+NXbHg==
Public Key: MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAE7xi6llyxv8G7BUzZMvNy/pvtmXLbY0MLTRQiWYt56VOLVIGQWG81Z5bPnGgcpYDLNzYGDh3XphfUt8CzL0xUyg==
TA License: MEQCIDpw5Yt5tQywUz7nVYc/uByePJmm/i/CiWxgoJbppbKHAiAH9+9VvSW10NRFxHJXerjZFyn13myZiOb7K9dfiHQxqA==

now run the VehicleClient with the arguments "[client_name][client_public_key][client_private_key][client_ta_license]":
an example using the vehile1 generated above would be:

java -jar VehicleClient.jar "Vehicle1" "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAE7xi6llyxv8G7BUzZMvNy/pvtmXLbY0MLTRQiWYt56VOLVIGQWG81Z5bPnGgcpYDLNzYGDh3XphfUt8CzL0xUyg==" "MD4CAQAwEAYHKoZIzj0CAQYFK4EEAAoEJzAlAgEBBCBTN10MDCq/UShGO6KZeoeRkFqC+4iAcw7ONDhS+NXbHg==" "MEQCIDpw5Yt5tQywUz7nVYc/uByePJmm/i/CiWxgoJbppbKHAiAH9+9VvSW10NRFxHJXerjZFyn13myZiOb7K9dfiHQxqA=="

the client should now be running!
Be sure to modify the port in the config.cfg file that has been generated if running multiple clients on the same machine.
