package example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DemoControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FileLoaderService fileLoaderService;
    private Map<String, Object> sessionAttributes;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        MvcResult result = mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/file/names"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();

        sessionAttributes = new HashMap<>();
        //Loop through all sessions attributes adding it to the session attributes hashmap
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            sessionAttributes.put(attributeName, session.getAttribute(attributeName));
        }
        //Temp fix while fileLoaderService is returning null
        when(fileLoaderService.uploadFile(any(MultipartFile.class),AuthLevel.USER))
                .thenReturn(Result.FILE_UPLOADED);
    }

    @Test
    public void testFileGetRoute() throws Exception{
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "var1,var2\n1,test test2".getBytes());
        mockMvc.perform(get("/file/get").param("file", "CAD.csv")
                        .sessionAttrs(sessionAttributes))
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadRoute() throws Exception{
        MockMultipartFile file = new MockMultipartFile("file", "example.csv", "text/plain", "var1,var2\n1,test test2".getBytes());
        mockMvc.perform(multipart("/file/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .sessionAttrs(sessionAttributes))
                .andExpect(status().isOk());
    }
}