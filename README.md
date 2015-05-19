# Overview

This library provides the ability to match a sequence of GPS coordinates to road segments based on the following paper:

Newson, Paul, and John Krumm. "Hidden Markov map matching through noise and sparseness." Proceedings of the 17th ACM SIGSPATIAL International Conference on Advances in Geographic Information Systems. ACM, 2009.

# Integation with other libraries
This map matching approach is based on a Hidden Markov Model (HMM) and hence uses the [hmm-lib](https://github.com/bmwcarit/hmm-lib).

The map matching approach is further based on distance computations between GPS coordinates and map matching candidates (i.e. road positions) as well as computing shortest routes between subsequent map matching candidates. Note that these computations are not provided by this library. However, an important design goal of this library is the easy integration of spatial and routing libraries that provide these computations.

# License

This library is licensed under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
