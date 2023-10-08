package com.example.demo;

import com.example.demo.model.MyUser;
import com.example.demo.repo.UserRepository;
import com.example.demo.security.JwtService;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
public class SecurityTest extends AbstractTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private final MyUser user = new MyUser();

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        user.setId(1);
        user.setUsername("usr");
        user.setPassword("$2a$10$x/YnJrdInIOGM.GSLq9YLOuvNtsZQiQmCWKS2QQ5UNG1G.vnAuIpy");
        user.setRole("USER");
        userRepository.save(user);
    }

    @Test
    public void _403Test() throws Exception {
        mvc.perform(get("/mirror")
                .contentType(MediaType.APPLICATION_JSON)
                .param("text", "text")
        )
                .andExpect(status().isForbidden());
    }

    @Test
    public void _200Test() throws Exception {

        UserDetails ud = User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
        var token = jwtService.generateToken(ud);
        mvc.perform(get("/mirror")
                .header("Authorization", "Bearer ".concat(token))
                .contentType(MediaType.APPLICATION_JSON)
                .param("text", "text")
        )
                .andExpect(status().isOk());
    }

    @Test
    public void badToken() {
        var fakeToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c3IiLCJleHAiOjE2MjQyNzE5NTMsImlhdCI6MTYyMzSwNzk1MywiYXV0aG9yaXRpZXMiOiJST0xFX1VTRVIifQ.Lk8BueSAr5u3mXDAsD3Zqv9qt31fRodPpbptmUkuVtI";

        assertThrows(MalformedJwtException.class, () -> mvc.perform(get("/mirror")
                .header("Authorization", "Bearer ".concat(fakeToken))
                .contentType(MediaType.APPLICATION_JSON)
                .param("text", "text")
        ));
    }
}
