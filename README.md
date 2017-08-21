# MetaboLights Agent

This module contains the USI MetaboLights Agent which is the agent in charge of mediating the comunication between USI and [MetaboLights](http://www.ebi.ac.uk/metabolights/). 
This agent will submit new metabolomics studies including metadata and raw data files, update and fetch existing ones. The agent will have an external dependency on Python API that will perform  
CRUD operations on the MetaboLights. The development of the Python API is planned. 

The agent 'listens' to two queues from RabbitMQ and processes three types of requests:
- Submission of new studies
- Update of existing studies
- Fetching existing studies

## About
This is a Spring Boot application, to run it you'll have to download the entire subs project, this agent parent project.
The agent is structured as follows:

- MetaboLightsAgentApplication
- agent/
  - Listener
  - SamplesProcessor
  - CertificatesGenerator
  - services/
    - Submission
    - Update
    - Fetch
  - converters/
    - Attribute BioSamples to USI
    - Attribute USI to BioSamples
    - Relationship BioSamples to USI
    - Relationship USI to BioSamples
    - Sample BioSamples to USI
    - Sample USI to BioSamples
    
## License
See the [LICENSE](../LICENSE) file in parent project for license rights and limitations (Apache 2.0).
