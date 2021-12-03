# Algorithms in Artificial Intelligence Course Final Project

### Implementing bayesian network structure with bayes ball and variable elimination algorithms.

## What is a Bayesian Network?

A Bayesian network (also known as a Bayes network, Bayes net, belief network, or decision network) is a probabilistic graphical model that represents a set of variables and their conditional dependencies via a directed acyclic graph (DAG).

## What is the purpose of Bayesian Networks?

Bayesian networks are ideal for taking an event that occurred and predicting the likelihood that any one of several possible known causes was the contributing factor. For example, a Bayesian network could represent the probabilistic relationships between diseases and symptoms. Given symptoms, the network can be used to compute the probabilities of the presence of various diseases.

* [taken from wikipedia](https://en.wikipedia.org/wiki/Bayesian_network)

## <i> Bayes Ball Algorithm </i>

### Algorithm Purpose:

To determine if two variables in the model graph are independent given other variables as evidence, the given conditional statement shown as ```Xa q Xb | Xc```  when we ask the algorithm if ```Xa``` and ```Xb``` are independents given ```Xc``` as evidence.

### Algorithm Pseudo-Code:

```
1. shade all the evidence nodes (Xc)

2. start at the soruce node (Xa)

3. search for the destination node (Xb) - (in this project I've used BFS algorithm)

4. if we can't reach from the source node to the destination node then nodes Xa and Xb (represents as variables) must be conditionaly independent.

5. else - there is a valid path between Xa and Xb nodes, then they must be conditionally dependent.
```

### Algorithm Example:

![bayes_ball_example.png](images/bayesball_example.png)

### Algorithm Source:

[Article of Ross D. Shachter who created this algorithm](https://arxiv.org/ftp/arxiv/papers/1301/1301.7412.pdf)


## <i> Variable Elimination Algorithm </i>

### Algorithm Purpose:

Variable elimination (VE) is a simple and general exact inference algorithm in probabilistic graphical models, such as Bayesian networks and Markov random fields. It can be used for inference of maximum a posteriori (MAP) state or estimation of conditional or marginal distributions over a subset of variables.

### Algorithm complexity:

The algorithm has exponential time complexity, but could be efficient in practice for the low-treewidth graphs, if the proper elimination order is used.

# TO BE CONTINUED...