

package esg_devops;

import br.com.fiap.esgresiduos.EsgApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = EsgApplication.class)
@ActiveProfiles("test")
class EsgDevopsApplicationTests {

    @Test
    void contextLoads() {
    }

}