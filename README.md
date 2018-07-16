# MetaboLights Agent

This module contains the USI MetaboLights Agent which is the agent in charge of mediating the comunication between USI and [MetaboLights](http://www.ebi.ac.uk/metabolights/). 
This agent will submit new metabolomics studies including metadata and raw data files, update and fetch existing ones. The agent will have an external dependency on Python API that will perform  
CRUD operations on the MetaboLights. The development of the Python API is planned. 

The agent 'listens' to two queues from RabbitMQ and processes three types of requests:
- Submission of new studies
- Update of existing studies
- Fetching existing studies
    
## License
See the [LICENSE](../LICENSE) file in parent project for license rights and limitations (Apache 2.0).
