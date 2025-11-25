# Petshop API

## Sobre o Projeto
O Petshop é um sistema de agendamento e gestão desenvolvido para modernizar o atendimento em petshops. A solução substitui métodos manuais e planilhas por um fluxo digital organizado, permitindo o controle de serviços, horários e funcionários. O projeto foi desenvolvido como parte do trabalho de conclusão de curso em Engenharia de Software pelo Centro Universitário Católica de Santa Catarina.

## Objetivo
Este software tem como objetivo centralizar a gestão do estabelecimento e simplificar o acesso dos clientes, proporcionando:
- **Eficiência Operacional:** Otimização da gestão de serviços e controle de agendas, reduzindo o tempo gasto com marcações manuais.
- **Redução de Conflitos:** Validação automática de horários para evitar agendamentos duplicados ou em horários indisponíveis.
- **Autonomia do Cliente:** Interface clara para que o cliente do pet visualize a disponibilidade e realize o agendamento.
- **Segurança:** Controle de acesso robusto para dados sensíveis de clientes e do negócio.

## Boas práticas de desenvolvimento aplicadas
- **Arquitetura MVC e Arquitetura de Camadas:** O projeto utiliza o padrão MVC para gerenciar as requisições HTTP e organiza o código em camadas lógicas (Controller, Service e Repository), garantindo a separação clara entre a interface da API, as regras de negócio e a persistência de dados.
- **Tratamento Global de Exceções**: Implementação de handlers para capturar erros e retornar respostas HTTP padronizadas e claras para o front-end.
- **Segurança com JWT**: Implementação de autenticação *stateless* via Token JWT e controle de acesso baseado em papéis (RBAC) para diferenciar Clientes, Funcionários e Administradores.
- **Testes Automatizados**: Cobertura de testes unitários e de integração utilizando JUnit, garantindo a confiabilidade das regras de negócio.
- **Documentação Automática**: Uso do Swagger/OpenAPI para documentar os endpoints, facilitando a integração com o time de front-end.

Esse é o BACK-END do projeto. O front-end está disponível no repositório: [FRONT-END](https://github.com/Matheus-Lz/petcare-app)

## Arquitetura e Modelagem
- [Diagrama de Casos de Uso]()
- [Diagrama C4]()

## Requisitos Funcionais
| Identificação | Requisito Funcional | Descrição |
|---------------|---------------------|-----------|
| **RF001** | **Cadastro de Usuários** | O sistema deve permitir o cadastro de novos usuários (clientes e funcionários). |
| **RF002** | **Autenticação** | O sistema deve permitir a autenticação de usuários por login e senha. |
| **RF003** | **Geração de Token** | O sistema deve gerar um token de acesso (JWT) após o login bem-sucedido. |
| **RF004** | **Recuperação de Senha** | O sistema deve oferecer funcionalidade de "esqueci minha senha" para redefinição segura. |
| **RF005** | **Atualização de Perfil** | O sistema deve permitir que usuários atualizem suas informações de perfil. |
| **RF006** | **Gestão de Funcionários** | O sistema deve permitir o cadastro, atualização e remoção de funcionários por um administrador. |
| **RF007** | **Definição de Funções** | O sistema deve permitir a definição de funções ou especialidades para os funcionários. |
| **RF008** | **Consulta de Funcionários** | O sistema deve permitir a consulta da lista de funcionários e seus detalhes. |
| **RF009** | **Gestão de Serviços** | O sistema deve permitir o gerenciamento (CRUD) de serviços (ex: banho, tosa). |
| **RF010** | **Detalhamento de Serviço** | Cada serviço deve possuir nome, descrição, duração estimada e preço. |
| **RF011** | **Agendamento** | O sistema deve permitir que o cliente agende um serviço com um horário disponível. |
| **RF012** | **Validação de Conflitos** | O sistema deve validar conflitos de horário para impedir agendamentos duplicados. |
| **RF013** | **Cancelamento** | O sistema deve permitir o cancelamento de agendamentos conforme regras de negócio. |
| **RF014** | **Visualização de Agenda** | O sistema deve permitir a visualização da agenda por dia. |

## Requisitos Não Funcionais
| Identificação | Requisito Não Funcional | Descrição |
|---------------|-------------------------|-----------|
| **RNF001** | **Criptografia** | As senhas devem ser armazenadas com criptografia. |
| **RNF002** | **Proteção de Rotas** | O acesso a rotas protegidas deve exigir token JWT válido. |
| **RNF003** | **Controle de Acesso (RBAC)** | Implementação de controle de acesso baseado em papéis (Cliente/Admin). |
| **RNF004** | **Documentação API** | Documentação automática da API via Swagger/OpenAPI. |
| **RNF005** | **Testes** | Cobertura de testes unitários e de integração (JUnit). |
| **RNF006** | **Padronização de Erros** | Respostas de erro da API devem ser padronizadas e claras. |

## Pipelines

O projeto utiliza pipelines automatizadas para deploy contínuo:

### 1. **Sonarcloud**
- Checkout do código e setup do Java 21.
- Execução de testes unitários e análise via **SonarQube**.
- Validação de coverage da aplicação.

### 2. **Deploy (AWS/Docker)**
- Build da aplicação Spring Boot.
- Criação e publicação da imagem Docker.
- Atualização do serviço na instância AWS.

## Tecnologias Utilizadas
- **Linguagem:** Java 21 com Spring Boot
- **Banco de Dados:** PostgreSQL
- **Autenticação:** JWT (Spring Security)
- **Testes:** JUnit e Mockito
- **Infraestrutura:** Docker e AWS
