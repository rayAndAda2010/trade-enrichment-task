See the [TASK](./TASK.md) file for instructions.

# Trade Enrichment Service
This service leverages the power of Spring WebFlux to create a highly responsive and scalable REST API. This API is designed to excel in data transformation tasks, ensuring seamless integration with various systems and applications.
Key Advantages of Using Spring WebFlux.

####Scalability

- Non-blocking I/O: Spring WebFlux employs a non-blocking I/O model, allowing it to handle thousands of concurrent connections efficiently. This scalability ensures our service remains robust under high load, accommodating growth without performance degradation.

####Performance

- Reactive Programming: By adopting reactive programming principles, we achieve better resource utilization and faster response times. The system reacts to events as they occur, reducing latency and improving overall application performance.

####Developer Productivity

- Simpler Code: With its declarative nature, Spring WebFlux simplifies the development process. Developers can focus on business logic rather than dealing with low-level details, leading to cleaner, more maintainable code.

####Integration

- Seamless Integration: Being part of the Spring ecosystem, Spring WebFlux integrates seamlessly with other Spring projects and third-party libraries. This compatibility streamlines the development process and enhances interoperability across different technologies.

####Future-proofing

- Adoption of Modern Standards: By choosing Spring WebFlux, we future-proof our project against evolving web standards and technologies. Its alignment with modern web protocols like HTTP/2 and WebSocket ensures long-term viability and adaptability.

###Core Concepts of Reactive Programming
- Mono: Represents a sequence of 0 or 1 items. A **Mono** is used when you expect either zero or one item to be emitted. It's useful for representing single values or the result of a computation that will produce exactly one value.
  ####
- Flux:  Represents a sequence of 0 to N items. A **Flux** is used when you expect zero, one, or many items to be emitted. It's ideal for representing collections of data or the result of a computation that could produce multiple values.
  ####
- Subscription: Represents the execution of a reactive pipeline. When you subscribe to a **Mono** or **Flux**, you're essentially starting the execution of the pipeline. The subscription object gives you control over the execution, allowing you to cancel the execution if needed.

####Example
```
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveExample {
    public static void main(String[] args) {
        // Creating a Mono that emits a single value
        Mono<String> mono = Mono.just("Hello");

        // Creating a Flux that emits multiple values
        Flux<Integer> flux = Flux.just(1, 2, 3);

        // Subscribing to the Mono and Flux
        mono.subscribe(value -> System.out.println("Mono: " + value));
        flux.subscribe(value -> System.out.println("Flux: " + value));

        // Output:
        // Mono: Hello
        // Flux: 1
        // Flux: 2
        // Flux: 3
    }
}
```
In this example, we create a Mono that emits a single string and a Flux that emits three integers. We then subscribe to both, printing the values they emit. This demonstrates the basic usage of Mono, Flux, and Subscription.

###How to run the service
    ```
    mvn package
    java -jar trade-enrichment-service-0.0.1-SNAPSHOT.jar
    ```
###How to use the API

    
- Send a POST request via curl command
    ```
    curl  -X POST -H "Content-Type: multipart/form-data" -F "file=@{your.dir}/trade-enrichment-task/src/test/resources/trade.csv" http://localhost:8080/api/v1/enrich
    ```

###Limitation of the code
- Some parameters are hardcoded (e.g. Cache expiry time)
- The current code does not have fine-grained control over the reactive stream（e.g. back pressure）
- The member field "price" within Trade should not be declared as String
- The product cache is managed by Spring Admin Server
- No API authorization
- No Https enabled
- No integration test


###Improvement
- Consider leverage Big data solution (e.g. Spark), not just reactive streaming
- Upgrade JDK 21 to leverage virtual thread
- Introduce GraalVM to have better performance
- Clean up unnecessary dependencies in pom.xml
- Run more performance test cases and fine-tune the code. (Spend about 10 seconds to process 1 million of trade data and 10k of product data)
- Add more method comments