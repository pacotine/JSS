# JSS
“Jealous Space Settlers” is a program simulating the distribution of resources to a group of space settlers.

## Context
This code is produced as part of a third year Bachelor degree mini-project.
The principal objectives were to acquire the ability to model problems within a OOP paradigm as a means of problem-solving
and to gain proficiency in the use of the Java programming language.

## Story
After each supply mission, the chief of a space colony must distribute critical resources among the settlers (colonists).
It is the responsibility of the chief to ensure that each settler receives one of these critical resources
(e.g. essential equipment or food rations).
In order to maintain harmony within the colony, the commander must ascertain each member's preferences for the allocated resources
and endeavour to respect them as far as is possible.
In addition to these preferences, the commander must consider the relationships between the settlers.
Some of them have poor relations with one another, and an unequal distribution of resources could result in conflict
or jeopardise the colony's survival.

# Installation

## Dependencies
### For all users
The minimum required JDK version for this project is 21.\
Java also needs to be installed on your machine (it is recommended to have [a configured PATH system variable for Java](https://www.java.com/en/download/help/path.html)).
### For developers (building from source and unit tests)
Please make sure you have [Maven](https://maven.apache.org/install.html) installed on your machine.\
Clone this repository and run the following commands in root folder downloaded:
- Clean and build the project with `mvn clean install`
- This will build and create a JAR executable file `target/JSS-[version].jar` where `[version]` is the release version
- For execution instructions, see [Run JAR](#run-jar) section
- You can also run tests with `mvn test`
## Run JAR
Download the latest version of the `.jar` file in [release section](https://github.com/pacotine/JSS/releases/tag/v1.0.0) of this repository then you can run the application
with the following command: 
```
java -jar path/to/JSS.jar
```
You can add arguments to the command to specify a file to read the colony from:
```
java -jar path/to/JSS.jar path/to/the/file.txt
```
By default, if no arguments are given, the application will ask you to create the colony manually.

# File format

The colony file follows a specific format. See an example in [assets](https://github.com/pacotine/JSS/blob/main/assets/colony.txt).
You have to add the following lines in this precise order:

- First, specify the settlers:
```
colon(settler_name).
```

- Second, add as many resources as settlers:
```
ressource(resource_name).
```
- Then, you can set bad relations between settlers (this is optional):
```
deteste(settler_name,settler_name).
```
- Finally, write for each settler the resources in order of preference:
```
preferences(settler_name,resource_name,...).
```

Note that the `.` at the end of each line is mandatory.


# Problem-solving
## Model
A simulation is the set of [resource allocation problem](https://en.wikipedia.org/wiki/Resource_allocation").
Given a set of agents $`N=\{1, \dots, n\}`$ and a set of indivisible resources $`O=\{o_{1}, \dots, o_{r}\}`$,
the number of resources is equal to the number of agents, _i.e._, $r = n$, and each settler
$i \in N$ should receive exactly one resource $o \in O$.

## Dispatch
One of the primary challenges of this project was to identify an effective method for allocating resources to the settlers. 
The objective was to ensure that the allocation of resources would have a minimal impact on the number of settlers who might be envious.

In order to achieve this, we have implemented two algorithms.

![](assets/jss-dispatch.png)
### Linear Dispatch
The most straightforward method for allocating resources to settlers is through linear dispatch. 
This approach involves assigning the most desired resource that is still available to each settler, in a sequential manner.
In reality, this process is $O(n)$ complex, with a significant drawback in that it fails to reduce the number of envious settlers.

### Switch (brute forcing) Dispatch
The switch dispatch consists of brute forcing the problem, by making $k$ random switches and 
keeping the changes only if it's a better configuration for this simulation.\
This method is also an approximation algorithm. However, compared to [MAX-LEF Dispatch method](#_max-lef_-dispatch)
it is far less efficient and effective.

### _**MAX-LEF**_ Dispatch
_**MAX-LEF**_ stands for _Maximum Local Envy Freeness_. It is an algorithm described in [this thesis paper](https://theses.hal.science/tel-03222104v1/document) by Anaëlle Wilczynski [[Section 4.4](https://theses.hal.science/tel-03222104v1/document#section.4.4)].
The main idea is to choose a specific order to apply a linear dispatch process that minimises local point conflicts by determining an independent set of settlers
in the relationships graph.

This algorithm is known to be NP-complete 
(as is the _**DEC-LEF**_ algorithm, which determines whether there is a solution without any jealousy),
so we use an approximation algorithm.
For a maximization problem, an algorithm is $p$-approximate, with $p \in [0, 1]$, if it outputs a solution
whose value is at least $p$-times the optimal value, for any instance.
More precisely, the algorithm implemented here is a $\frac{|I|}{n}$-approximate algorithm of _**MAX-LEF**_.
So, in order to obtain better results and tend towards the optimal, we repeat this algorithm on various instances
of the simulation with a different running order each time
(each instance can give a different set of independent settlers $I$, and thus play on the result).

We believe this to be the best possible solution to the problem in a realistic time complexity.
This approach represents an optimal compromise between the time required for execution and the level of satisfaction with the result.

# Microbenchmarking
We used [JMH](https://openjdk.org/projects/code-tools/jmh/) for microbenchmarking the three algorithms in this project. Below is a detailed breakdown of our benchmarking process, results, and observations.

## Benchmark setup
The benchmarks were conducted on the following system configuration:
- **CPU**: 12th Gen Intel i7-1250U (12)
- **OS**: Ubuntu 24.10 (x86_64)
- **JDK**: openjdk 21.0.5
- **JMH Core**: [1.37](https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-core/1.37)
- **JMH Annotation Processors**: [1.37](https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-generator-annprocess/1.37)

## Benchmark configuration
The benchmarking process was performed using JMH with the following parameters:
- **Warm-up**: 3 iterations
- **Measurement iterations**: 10 iterations
- **Forks**: 1 fork

Each benchmark scenario tested the algorithms under different conditions of colony size and graph density, 
as shown below:

|                     | benchmark100 | benchmark500 | benchmark1000 |
| ------------------- | ------------ | ------------ | ------------- |
| **colony size**     | 100          | 500          | 1000          |
| **graph density**\* | ~50%         | ~64%         | ~84%          |

\*the density of the graph is defined in this context as the ratio between the average
number of bad relations of the settlers and the size of the colony (for example, in the *benchmark100* scenario,
every settler has an average of 50 bad relationships).

## Results
Please note that `switch` and `MAX-LEF` parameters are $n$.

|                   | algorithm | average time (ms) | average output |
|-------------------|-----------|-------------------|----------------|
|                   | `linear`  | 0.080 ⭐           | 34             |
| **benchmark100**  | `switch`  | 25.593            | 32             |
|                   | `MAX-LEF` | 19.651            | 28  ⭐          |
|                   |           |                   |
|                   | `linear`  | 4.104 ⭐           | 208            |
| **benchmark500**  | `switch`  | 2668.919          | 202            |
|                   | `MAX-LEF` | 1855.284          | 188 ⭐          |
|                   |           |                   |
|                   | `linear`  | 15.261 ⭐          | 469            |
| **benchmark1000** | `switch`  | 36762.073         | 463            |
|                   | `MAX-LEF` | 20951.470         | 444 ⭐          |

⭐ *is the best performance between algorithms.*
## Performance comparison
Key observations:
- `linear` is the fastest but provides suboptimal results in terms of minimizing jealous settlers
- `switch` minimizes jealousy well but is significantly slower, especially for larger colony sizes
- `MAX-LEF` offers the best balance between execution time and output quality, making it the most practical solution for large colonies

Our [MAX-LEF dispatch method](#_max-lef_-dispatch) outperforms in terms
of minimizing the number of jealous settlers, for a lower execution time than the classic brute force switch algorithm.
These figures show how $n$ instances of `MAX-LEF` can be an optimal compromise between execution time
and average minimization result.

## Assets
You can find the JMH output file [here](https://github.com/user-attachments/files/18187016/benchmark.txt).
All input files are also available in the [assets folder](assets/benchmark).

# Optimum resolution
[MAX-LEF is a p-approximation algorithm](#_max-lef_-dispatch), meaning it provides a theoretical approximation bound for solving 
instances of this problem efficiently. While it offers practical solutions with a good balance of performance and quality, 
solving this problem exactly requires other, more computationally intensive approaches.

## Solving the problem exactly
One exact approach to solving this problem is through [integer (linear) programming](https://en.wikipedia.org/wiki/Integer_programming).
These methods formulate the problem mathematically, enabling solvers to find the optimal solution. For a detailed 
overview of mathematical optimization and integer programming, see [this paper of the MIT](https://web.mit.edu/15.053/www/AMP-Chapter-09.pdf).\
In practice, a LP/MIP solver like [the GLPK](https://www.gnu.org/software/glpk/) could be used if the problem is correctly formulated as a LP problem.
For Java-based projects like ours, [GLPK for Java](https://glpk-java.sourceforge.net/) provides a convenient interface to leverage these capabilities.

## Challenges of exact solutions
While ILP/MIP solvers can theoretically find exact solutions, it is important to note that:
- **The problem remains NP-complete**: this means that as the size of the problem grows, the computational complexity 
increases exponentially, making exact solutions impractical for large instances
- **Hard-to-solve instances**: even with advanced solvers, certain problem instances may be computationally challenging, 
requiring significant resources or resulting in long solving times

## Practical implications
Due to these limitations, approximation algorithms like **MAX-LEF** offer a more practical alternative. 
They achieve near-optimal results with significantly lower computational overhead, and making them well-suited for 
real-world applications where scalability and efficiency are critical.