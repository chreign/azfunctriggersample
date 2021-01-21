# azfunctiontimer
build before run, most of command does not require run in the project folder, but some of them requires so, it is better to stay in the project folder

```sh
mvn clean package
```

## run in local
```sh
mvn azure-functions:run
```
If you see below error, you can try change to "AzureWebJobsStorage": "UseDevelopmentStorage=true" in local.settings.json
```sh
Missing value for AzureWebJobsStorage in local.settings.json. This is required for all triggers other than httptrigger, kafkatrigger. You can run 'func azure functionapp fetch-app-settings <functionAppName>' or specify a connection string in local.settings.json.
```
## deploy to azure
```sh
mvn azure-functions:deploy
```
## run in azure
### pre set up
```sh
export location=<azure-region>
export resourceGroup=<resource-group-name>
export app=<name-it>
export kvName=<keyvault-name>
```
### assign an identity to the application
```sh
az functionapp identity assign --name $app --resource-group $resourceGroup
```
get the principalId from above result
### set up the KeyVault
```sh
export principalId=<the principal Id above>
az keyvault create -n $kvName -g $resourceGroup -l $location
az keyvault set-policy --name <policy name> --object-id $principalId --secret-permissions get list
az keyvault secret set --vault-name $kvName -n mySecret --value 'sample'
```
get the id from above result
### update the AppSettings
```sh
update the appsettings.json for mySecret to "@Microsoft.KeyVault(SecretUri=<id get above>)"
az functionapp config appsettings set -g $resourceGroup -n $app --settings @appsettings.json
```
### verify the update (optional)
```sh
az functionapp config appsettings list -g $resourceGroup -n $app 
```
### check the log
```sh
func azure functionapp logstream $app
```
## Maintainance
### stop the app
```sh
az functionapp start --name $app --resource-group $resourceGroup
```
### start the app
```sh
az functionapp stop --name $app --resource-group $resourceGroup
```
### restart the app
```sh
az functionapp restart --name $app --resource-group $resourceGroup
```
## note
- Don't delete the keyvault since you cannot recover it by the defualt permission so you cannot reuse the same name
- Once you set/change the keyvault secret value, you need to update the appsettings.json to use the latest reference refer to [update the AppSettings](#update-the-appsettings) 
- Update the appsettings will restart the app automatically
- It's better to try out the SpringbootFunction and Scheduler(Spring) sepereately since it will impact the life cycle when run them together
