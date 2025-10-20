Aplicativo mobile do **Aion**, plataforma da **Zyntra** para gestão inteligente de absenteísmo e bem-estar corporativo. Este repositório contempla a versão mobile utilizada por colaboradores para registro de presença, justificativas, acompanhamento de desempenho e comunicação com o RH.

---

## 🧭 Visão Geral

O Aion Mobile é parte do ecossistema modular da Zyntra, promovendo uma jornada fluida, intuitiva e centrada no colaborador. Construído com foco em performance, escalabilidade e experiência do usuário, atende empresas que valorizam produtividade com empatia.

---

## ⚙️ Stack Tecnológica

- **Framework**: Android Studios
- **Linguagem**: Java
- **API**: REST 
- **Autenticação**: Basic
- **CI/CD**: GitHub Actions

---

## 🧪 Ambiente de Desenvolvimento

### Instalação:
```bash
git clone https://github.com/zyntra/aion-mobile.git
cd aion-mobile
```

---

## 📁 Estrutura de Pastas

```bash
src/
├── java/
│   └── com.aula.aion/
│       ├── adapter/          # Classes de adaptação (Adapters para RecyclerView, ListView, etc)
│       ├── api/              # Comunicação com APIs e serviços externos
│       ├── model/            # Classes de modelo (entidades e dados)
│       ├── notification/     # Lógica relacionada a notificações
│       ├── ui/               # Lógica de todas as tela mobile
│       └── widgets/          # Telas principais e componentes de UI
│       ├── EditarPerfil      # Tela de edição de perfil
│       ├── Inicio            # Tela inicial do app
│       ├── Login             # Tela de login
│       ├── LogoutCallback    # Callback para logout
│       ├── NotificacaoActivity # Tela de notificações
│       ├── Perfil            # Tela de perfil
│       └── SplashScreen      # Tela de splash (inicial)
│
├── res/                         # Recursos do aplicativo
│   ├── anim/                    # Animações XML
│   ├── color/                   # Definições de cores
│   ├── drawable/                # Imagens vetoriais e assets gráficos
│   ├── font/                    # Fontes personalizadas
│   ├── layout/                  # Layouts XML das telas
│   ├── menu/                    # Menus XML (Toolbar, BottomNav, etc)
│   ├── mipmap/                  # Ícones do aplicativo (launcher)
│   ├── navigation/              # Gráficos de navegação (NavGraph)
│   └── values/                  # Strings, dimensões, estilos e temas

```

---

## ✅ Boas Práticas

- Commits semânticos (padronização com Conventional Commits)
   - "fix:..."      - Commits de correção de bugs ou conflitos solucionados.
   - "feat:..."     - Commits que indicam que seu trecho de código está incluindo um novo recurso (Funcionalidade nova).
   - "style:..."    - Commits do tipo style indicam que houveram alterações referentes a formatações de código (Não inclui alterações em código).
   - "refactor:..." - Commits do tipo refactor referem-se a mudanças em relação ao funcionamento. Ex.: Mudei a forma como é feita o envio de email. (Não mudei porque estava errado e sim porque ou uma refatoração na regra de negócio)
   - "build:..."    - Commits do tipo build são utilizados quando são realizadas modificações em arquivos de build e dependências.
   - "del:..."      - Commits que indicam a deleção de itens do projeto.

- PRs com descrição clara e checklist de revisão
- Testes unitários obrigatórios para novas funcionalidades
- Revisões semanais de dependências (segurança e performance)

---

## 🚀 Roadmap (Q3 2025)

- [x] Dashboard de bem-estar para o colaborador
- [x] Suporte a múltiplas filiais/empresas

---

## 👥 Contribuidores

- **Desenvolvedores**: [Vinicius Abs Soares, Jefferson Custodio Lopes]

---

## 🛡️ Licença

Projeto proprietário da **Zyntra Tecnologia Ltda.**  
Todos os direitos reservados.
