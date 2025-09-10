# Tecnologias e Arquitetura do Backend

### **Framework Principal**
- **Spring Boot 2.7.18**: Framework principal para desenvolvimento de APIs REST
- **Java 17**: Linguagem de programação com suporte a Records e recursos modernos
- **Maven**: Gerenciador de dependências e build

### **Banco de Dados**
- **PostgreSQL**: Banco relacional principal para produção
- **H2**: Banco em memória para desenvolvimento e testes
- **JPA/Hibernate**: ORM para mapeamento objeto-relacional

### **Documentação e Validação**
- **SpringDoc OpenAPI**: Documentação automática da API (Swagger)
- **Bean Validation**: Validação de dados de entrada
- **Lombok**: Redução de boilerplate code

### **Testes**
- **JUnit 5**: Framework de testes unitários e de integração
- **Mockito**: Framework para mocking em testes unitários
- **Spring Boot Test**: Testes de integração com contexto Spring
- **Testcontainers**: Testes com containers Docker (configurado mas não utilizado)

## Algumas decisões arquiteturais do projeto: 

### **1. DTOs (Data Transfer Objects)**
- **Java Records**: DTOs imutáveis e concisos
- **Separação de camadas**: APIs não expõem entidades diretamente
- **Validação centralizada**: Validações no DTO, não na entidade

```java
public record HeroRequestDTO(
    @NotBlank String nome,
    @NotBlank String nomeHeroi,
    @NotNull LocalDate dataNascimento,
    @Positive Double altura,
    @Positive Double peso,
    @NotEmpty List<Long> superpoderIds
) {}
```

### **2. Tratamento Global de Exceções**
- **@RestControllerAdvice**: Interceptação centralizada de exceções
- **Exceções customizadas**: `ResourceNotFoundException`, `DuplicateHeroNameException`
- **Respostas padronizadas**: `ErrorResponse` com estrutura consistente

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, "Not Found", ex.getMessage(), ex.getPath()));
    }
}
```

### **3. Gerenciamento de Transações**
- **@Transactional**: Controle explícito de transações
- **Métodos públicos**: Todos os métodos de serviço são transacionais
- **Lazy Loading**: Controle manual de carregamento de coleções

### **4. Configuração de Banco de Dados**

#### **H2 (Desenvolvimento)**
```yaml
spring:
  profiles:
    active: h2
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
```

#### **PostgreSQL (Produção)**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/heroes_db
    username: heroes_user
    password: heroes_password
  jpa:
    hibernate:
      ddl-auto: update
```

### **5. `spring.jpa.open-in-view: false`**

**Por que desabilitar?**
- **Performance**: Evita sessões Hibernate muito longas
- **Controle**: Força carregamento explícito de dados lazy
- **Memória**: Reduz consumo de memória
- **Debugging**: Facilita identificação de problemas de lazy loading

**Como funciona:**
```java
@Transactional
public HeroResponseDTO findHeroById(Long id) {
    Hero hero = findHeroByIdOrThrow(id);
    // Acesso aos superpoderes dentro da transação
    Set<Superpoder> superpoderes = hero.getSuperpoderes();
    return convertHeroToResponseDTO(hero);
}
```

**Benefícios:**
- Sessão Hibernate fecha após cada requisição
- Força carregamento explícito de dados relacionados
- Melhora performance em aplicações com muitas requisições
- Evita `LazyInitializationException`

### **6. Inicialização de Dados**

**DataInitializer**: Classe que popula o banco com dados iniciais (usado somente para popular banco)
```java
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Transactional
    public void run(String... args) {
        createSuperpoderes();
        createHeroes();
    }
}
```

### **7. Validação de Negócio**

**Camada de Serviço:**
```java
private void validateHeroNameUniqueness(String nomeHeroi) {
    if (heroRepository.existsByNomeHeroi(nomeHeroi)) {
        throw new DuplicateHeroNameException(nomeHeroi);
    }
}
```

**Características:**
- Validações de negócio no Service, não no Controller
- Exceções customizadas com mensagens específicas
- Validação de unicidade no banco de dados

### **8. Mapeamento DTO ↔ Entity**

**HeroMapper**: Conversão entre camadas
```java
public Hero toEntity(HeroRequestDTO dto) {
    Hero hero = new Hero();
    hero.setNome(dto.nome());
    hero.setNomeHeroi(dto.nomeHeroi());
    // ... outros campos
    return hero;
}
```

**Características:**
- Mapeamento manual (simples e controlado)
- Conversão de tipos (String → LocalDate)
- Associação de entidades relacionadas
- Separação clara entre DTOs e entidades

## 🧪 Estratégia de Testes

### **Testes Unitários**
- **Mockito**: Mock de dependências externas
- **JUnit 5**: Framework de testes
- **@ExtendWith(MockitoExtension.class)**: Configuração automática

```java
@ExtendWith(MockitoExtension.class)
class HeroServiceTest {
    @Mock
    private HeroRepository heroRepository;
    
    @InjectMocks
    private HeroService heroService;
}
```

### **Testes de Integração**
- **@SpringBootTest**: Carrega contexto completo do Spring
- **@DataJpaTest**: Testes específicos para JPA
- **H2**: Banco em memória para testes

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class HeroControllerIntegrationTest {
    // Testes com contexto Spring completo
}
```

### **Cobertura de Testes**
- **87 testes** executando com sucesso
- **Cobertura**: Controllers, Services, Repositories
- **Cenários**: Casos de sucesso e erro
- **Validações**: DTOs, exceções, regras de negócio

## 📊 Métricas e Performance

### **Configurações de Performance**
- **Connection Pool**: HikariCP (padrão do Spring Boot)
- **Lazy Loading**: Configurado para melhor performance
- **Batch Processing**: Para operações em lote
- **Caching**: Preparado para implementação futura

### **Monitoramento**
- **Actuator**: Endpoints de saúde e métricas
- **Health Checks**: Para Docker Compose
- **Logging**: Configurado para diferentes ambientes

