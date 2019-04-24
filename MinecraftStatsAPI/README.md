# The backend of the project
This part of the project serves as a API to securely connect to the SQL instance.  
It can handle timeline requests, with or without user and specific block type, and total mining statistics overview of each user.
It needs to be served over the internet using a Node.js (or capable) server.

### Configuration
Edit the `config.json` file to include your own credentials. Set a specific server port if you wish to do so.  
The table names are the default names, however if you use a prefix (e.g. 'mc_') then edit these fields to include the prefix.

