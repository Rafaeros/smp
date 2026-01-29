package br.rafaeros.smp.core.config;

import br.rafaeros.smp.modules.user.model.User;
import br.rafaeros.smp.modules.user.model.enums.Role;
import br.rafaeros.smp.modules.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); 
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
                
                System.out.println("✅ [Seeder] Usuário ADMIN criado com sucesso (user: admin / pass: admin123)");
            } else {
                System.out.println("ℹ️ [Seeder] Usuário ADMIN já existe. Pulando criação.");
            }
        };
    }
}
