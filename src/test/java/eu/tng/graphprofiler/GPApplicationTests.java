package eu.tng.graphprofiler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GPApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Ignore
    @Test
    public void checklamdafuntionofjava8() {

        System.out.println("checklamdafuntionofjava8");

        List<String> list = Arrays.asList("Apple", "Orange", "Banana");
        String string = "A box of Oranges";
        boolean match = list.stream().anyMatch(s -> string.contains(s));
        System.out.println(match);

        List<String> list1 = Arrays.asList("Apple", "Orange", "Banana", "an");

        Stream<String> match1 = list1.stream().filter(s-> s.contains("an"));
        
       match1.forEach(System.out::println);
    }
    
    
    
}
