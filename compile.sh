#!/bin/bash
mvn clean install
rm -rf ~/Documents/dev/minecraft/mprison_server/plugins/MPrison*
mv target/MPrison-*.jar ~/Documents/dev/minecraft/mprison_server/plugins
rm -rf target
echo "Deleted old MPrison plugin directory and plugin jar, moved newly compiled jar into MPrison server."