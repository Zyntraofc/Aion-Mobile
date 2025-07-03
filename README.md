Aplicativo mobile do **Aion**, plataforma da **Zyntra** para gestão inteligente de absenteísmo e bem-estar corporativo. Este repositório contempla a versão mobile utilizada por colaboradores para registro de presença, justificativas, acompanhamento de desempenho e comunicação com o RH.

---

## 🧭 Visão Geral

O Aion Mobile é parte do ecossistema modular da Zyntra, promovendo uma jornada fluida, intuitiva e centrada no colaborador. Construído com foco em performance, escalabilidade e experiência do usuário, atende empresas que valorizam produtividade com empatia.

---

## ⚙️ Stack Tecnológica

- **Framework**: React Native (Expo)
- **Linguagem**: TypeScript
- **Gerenciamento de Estado**: Zustand + Context API
- **API**: REST (Axios)
- **Autenticação**: OAuth2 com refresh token
- **CI/CD**: EAS (Expo Application Services)
- **Armazenamento Local**: AsyncStorage
- **Monitoramento**: Sentry + Firebase Crashlytics

---

## 🔑 Funcionalidades Core

- Login seguro com autenticação multifator
- Registro de ponto (geolocalizado e com selfie, se habilitado)
- Justificativas de ausência com anexo de documentos
- Timeline de presença e faltas
- Feedback de performance (KPI de presença)
- Comunicação com RH (mensagens e notificações push)

---

## 🧪 Ambiente de Desenvolvimento

### Requisitos:
- Node.js 18+
- Yarn 1.22+
- Expo CLI
- Conta no Expo Go
- `.env` com variáveis de ambiente (modelo disponível em `.env.example`)

### Instalação:
```bash
git clone https://github.com/zyntra/aion-mobile.git
cd aion-mobile
yarn install
cp .env.example .env
expo start
```

---

## 📁 Estrutura de Pastas

```bash
src/
├── assets/              # Imagens e ícones
├── components/          # Componentes reutilizáveis
├── contexts/            # Context API Providers
├── hooks/               # Custom hooks
├── screens/             # Telas da aplicação
├── services/            # Serviços (API, Storage, etc)
├── store/               # Zustand stores
├── styles/              # Temas e estilos globais
├── utils/               # Funções utilitárias
```

---

## ✅ Boas Práticas

- Commits semânticos (padronização com Conventional Commits)
   - "fix:..."      - Commits de correção de bugs ou conflitos solucionados.
   - "feat:..."     - Commits que indicam que seu trecho de código está incluindo um novo recurso (Funcionalidade nova).
   - "style:..."    - Commits do tipo style indicam que houveram alterações referentes a formatações de código (Não inclui alterações em código).
   - "refactor:..." - Commits do tipo refactor referem-se a mudanças em relação ao funcionamento. Ex.: Mudei a forma como é feita o envio de email. (Não mudei porque estava errado e sim porque ou uma refatoração na regra de negócio)
   - "build:..."    - Commits do tipo build são utilizados quando são realizadas modificações em arquivos de build e dependências.

- PRs com descrição clara e checklist de revisão
- Testes unitários obrigatórios para novas funcionalidades
- Revisões semanais de dependências (segurança e performance)
- Feature toggles para funcionalidades ainda não liberadas

---

## 🚀 Roadmap (Q3 2025)

- [ ] Módulo de saúde emocional (pulse check)
- [ ] Integração com calendário de férias
- [ ] Dashboard de bem-estar para o colaborador
- [ ] Suporte a múltiplas filiais/empresas

---

## 👥 Contribuidores

- **Product Owner**: [Nome do PO]
- **Tech Lead Mobile**: [Nome do TL]
- **UX/UI Designer**: [Nome do Designer]
- **Desenvolvedores**: [Lista da Squad Mobile]

---

## 🛡️ Licença

Projeto proprietário da **Zyntra Tecnologia Ltda.**  
Todos os direitos reservados.
