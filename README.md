# coreprotect-statistics
Utilities and front end to create a mining statistics overview from CoreProtect

Running example here: https://pacstats.ylyl.nl/

# Installation and requirements
This software can be freely used to create a statistics overview for own non commerical use.  
Make sure to have:
* Minecraft server
* CoreProtect installed and configured to use MySQL as storage
* The database login credentials
* The knowledge on how to add tables and columns in MySQL
* A computer capable of running a Java application, a web server and a NodeJS project.

Installation steps:
1. Begin by uploading the files onto the computer. `MinecraftStats` `MinecraftStatsAPI` `MinecraftStatsFront`.
2. Login to your MySQL instance. Go to your coreprotect database.
3. Create a table for the statistics:
```
CREATE TABLE `stats` (
  `user` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `total` int(11) NOT NULL
)
```
4. Create a empty table for prettified statistics:
```
CREATE TABLE `stats_pretty` (
 `user` int(11) NOT NULL
)
```
5. Alter table `block` to include `mined_first` column with a default value of 0
```
ALTER TABLE `block` ADD `mined_first` INT NOT NULL DEFAULT '0' AFTER `rolled_back`;
```
6. Follow the configuration of the Java program: https://github.com/Bernardez/coreprotect-statistics/tree/master/MinecraftStats#configuration
7. Follow the configuration of the backend: https://github.com/Bernardez/coreprotect-statistics/tree/master/MinecraftStatsAPI#configuration
8. Follow the configuration of the frontend: https://github.com/Bernardez/coreprotect-statistics/tree/master/MinecraftStatsFront#configuration
