# 🔗 Nu Shortener App

Este é um aplicativo Android moderno focado em encurtamento de URLs, construído para demonstrar a implementação robusta de **Clean Architecture** e o padrão **MVI (Model-View-Intent)**, utilizando **Jetpack Compose** e **Hilt**. O projeto também inclui a manipulação avançada de Deep Links (App Links) para o fluxo de redirecionamento.

-----

## 🎯 Objetivo do Projeto

1.  Permite ao usuário encurtar links.
2.  Exibe um histórico dos links recentemente encurtados.
3.  Demonstra um fluxo de Deep Link completo: o app intercepta o link encurtado, busca a URL original na API e redireciona o usuário para o navegador.

-----

## 🏗️ Arquitetura e Padrões de Design

O projeto segue rigorosamente o padrão **Clean Architecture** para garantir o desacoplamento, testabilidade e manutenibilidade do código.

### 1\. Separação de Conceitos (Clean Architecture)

| Camada | Responsabilidade | Tecnologias Chave |
| :--- | :--- | :--- |
| **Presentation (UI)** | Exibir o estado (`State`), capturar interações (`Intent`). | Jetpack Compose, MVI, ViewModels |
| **Domain** | Lógica de Negócio (Use Cases) e Modelos de Domínio. | Kotlin, Use Cases |
| **Data** | Comunicação com API (Implementações de Repositório). | Retrofit, OkHttp, Repositories |

### 2\. Gerenciamento de Estado: MVI (Model-View-Intent)

A camada de UI adota o **MVI** para um fluxo de dados unidirecional e previsível:

  * **State:** Representação imutável do estado atual da tela.
  * **Intent:** Ações do usuário ou do sistema que disparam mudanças de estado.
  * **Effect:** Eventos únicos de UI (e.g., `ShowToast`, `CopyUrlToClipboard`).

### 3\. Injeção de Dependência

A injeção de dependência é gerenciada pelo **Hilt/Dagger**, resolvendo automaticamente as dependências do projeto (Repositórios, Services e ViewModels) com o escopo de ciclo de vida adequado.

-----

## 🔗 Funcionalidade de App Links

O principal desafio é garantir que o aplicativo intercepte o link encurtado e inicie o fluxo de busca e redirecionamento.

**Endpoint Capturado:** `https://url-shortener-server.onrender.com/api/alias/{ID}`

### Fluxo de Redirecionamento

1.  O usuário clica no link encurtado em qualquer aplicativo externo (e.g., chat).
2.  A **`MainActivity`** do aplicativo intercepta o link (via `Intent.ACTION_VIEW`).
3.  O `MainViewModel` utiliza o **`GetOriginalUrlUseCase`** para buscar a URL completa na API.
4.  O aplicativo inicia um novo `Intent` para abrir a URL original no navegador do dispositivo.

-----

## 🧪 Instruções para Teste (ADB)

A verificação automática de App Links falha no domínio de teste (`onrender.com`). Para testar a funcionalidade, você deve forçar a associação do link ao seu aplicativo via ADB.

1.  **Instale o aplicativo** no dispositivo/emulador.

2.  **Force o tratamento de links** (Necessário para ignorar a validação de domínio. Substitua `com.example.nu` pelo seu Application ID, se necessário):

    ```bash
    adb shell pm set-app-links --package com.example.nu 1 all
    ```

3.  **Simule o clique no link** para iniciar o fluxo de Deep Link:

    ```bash
    adb shell am start -d "https://url-shortener-server.onrender.com/qualquerID" -a android.intent.action.VIEW com.example.nu
    ```

**Resultado Esperado:** O aplicativo deve ser lançado, buscar a URL original na API e, em seguida, abrir essa URL no navegador padrão do dispositivo.
