# Overview

This project demonstrates how to use the [hmm-lib](https://github.com/bmwcarit/hmm-lib) for 
matching a sequence of GPS coordinates to roads (called offline map matching) but does not provide
integration to any particular map.

This map matching approach is based on Hidden Markov Models (HMM) and described in the following
paper:  
*Newson, Paul, and John Krumm. "Hidden Markov map matching through noise and sparseness." 
Proceedings of the 17th ACM SIGSPATIAL International Conference on Advances in Geographic 
Information Systems. ACM, 2009.*

# Integation with other libraries
To make map matching work with an actual map the following needs to be further implemented:
* compute map matching candidates (i.e. possible road positions) for each GPS position
* compute distances between GPS positions and map matching candidates
* compute shortest routes between subsequent map matching candidates

These computations are not provided by this project because they are usually dependent on the
underlying map. Moreover, this lets the user choose his own favorite geospatial/routing libraries. 

# License

This library is licensed under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
