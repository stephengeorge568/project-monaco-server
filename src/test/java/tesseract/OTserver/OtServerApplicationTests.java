package tesseract.OTserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;

@SpringBootTest
class OtServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void flex() {
		System.out.println(SpringVersion.getVersion());
	}

}
