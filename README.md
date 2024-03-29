
# PostFinder
This repository contains the source code implementation of PostFinder and the datasets used to replicate the experimental results of our paper which has been accepted for publication in Information and Software Technology Journal:

_PostFinder: Mining Stack Overflow posts to support software developers_

A pre-print version of the paper is available [here](https://github.com/MDEGroup/PostFinder/blob/master/IST-SOrec.pdf).

## Introduction
During the development of complex software systems, programmers look for external resources to understand better how to use specific APIs and to get advice related to their current tasks. Stack Overflow provides developers with a broader insight of API usage and with useful code examples. However, finding Stack Overflow posts that are relevant to the current context is a strenuous task. In this paper, we introduce PostFinder, an approach that allows developers to retrieve messages from Stack Overflow being relevant to the API function calls that they have already defined, as well as to the external libraries included in the project being developed. The approach has been validated by means of a user study involving 11 developers to evaluate 500 posts with respect to 50 contexts. Experimental results indicate the suitability of PostFinder to recommend relevant Stack Overflow posts and concurrently show that the tool outperforms a well-established baseline.

## Repository Structure
This repository is organized as follows:

* The [tools](./tools) directory contains the implementation of PostFinder we developed;
* The [user-study](./user-study) directory contains the user study conducted to evaluate FaCoY and PostFinder. In particular, this [excel file](./user-study/UserEvaluationResults.xlsx) provides a summary on the result.

## Questionnaires
The following links provide the questionnaires we have conducted to evaluate FaCoY and PostFinder:

* [First questionnaire](https://docs.google.com/forms/d/e/1FAIpQLScyfDQsf5wz0gze6Z_CZfhrqy71f4h8KiWl-nX-6vm0rh2YlA/viewform?usp=pp_url);
* [Second questionnaire](https://docs.google.com/forms/d/e/1FAIpQLSdngaMJKtA3cCtjnzz2Br3qEGUV5ok1P9MkWAtP-PmjHcpa0A/viewform);
* [Third questionnaire](https://docs.google.com/forms/d/e/1FAIpQLSezCjqRAWLSD14pLHnrWA2QDvlQAdHtXRoL74iZzbMz5lCmeQ/viewform?usp=pp_url);
* [Fourth questionnaire](https://docs.google.com/forms/d/e/1FAIpQLSeETcUGH5zpJyS7k3i0MJx86jUaJ4ti5Pj-SYe6f1IoOheseg/viewform?usp=pp_url);
* [Fifth questionnaire](https://docs.google.com/forms/d/e/1FAIpQLSel1fh8mp7vMCFf0XaTlFWyq0xQ8scHjbPS5tqqzfG_3wW2NQ/viewform?usp=pp_url).



```
mvn exec:java -Dexec.mainClass="soRec.Main" -Dexec.args="-indexFolder /path/to/luceneIndex -queryFolder /path/to/codeContexts"
```




## How to cite
If you find our work useful for your research, please cite the paper using the following BibTex entry:

```
@article{RUBEI2020106367,
title = "PostFinder: Mining Stack Overflow posts to support software developers",
journal = "Information and Software Technology",
volume = "127",
pages = "106367",
year = "2020",
issn = "0950-5849",
doi = "https://doi.org/10.1016/j.infsof.2020.106367",
url = "http://www.sciencedirect.com/science/article/pii/S0950584920301361",
author = "Riccardo Rubei and Claudio {Di Sipio} and Phuong T. Nguyen and Juri {Di Rocco} and Davide {Di Ruscio}",
keywords = "Recommender systems, Mining Stack Overflow posts, Indexing posts"}

```

