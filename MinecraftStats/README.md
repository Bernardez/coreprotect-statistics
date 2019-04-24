# The poor-mans strategy statistics generator
This part of the project serves as a statistics generator. The executable `update-stats.jar` can be run to create or update statistics.  
Due to the appoach this project takes to get the statistics (read as; not efficiently) it won't be prevered to run this executable more than once an hour. For extremely large sets of data it might be even better to run this once a day.

### Configuration
To run the executable use the command `java -jar update-stats.jar`.  
Running the generator for the first time will result in a file called `config.properties` to be created in the same directory as the user currently is in. (cwd)  
Edit the `config.properties` file to include your own credentials. If your coreprotect tables use a specific prefix make sure to add them now. (e.g. `table.block` = `mc_block` instead of just `block`)  
Running the generator when the config.properties file is present in the same directory as the user currently is in will result in the stats being generated.  

### Note
The generator reads the entire table `block` this table can be several milions of records large. The program can fail due to memory limitations, this has not been tested.  
MYSQL might protest about `maximum connections reached` or something in the lines of that. It is possible to edit the MYSQL max settings variables to get it to work.  
The table `block` will include a `mined_first` row. When a value 1 is present it means that for these exact coordinates this was the first block update. (can be either mined or placed)
The table `stats` is a counted table of the `mined_first` values. Where for each user and each block type a counted value will be added.
The table `stats_pretty` isn't pretty per definition. However this table is the short but wide version of `stats`. Each user is listed, together with a count of first mined for each block type.
The table `material_map` is used to retrieve names for each block type. If you encounter wrong names due to legacy database entrys, please let me know about it.

