# CortaJá V2 — Relatório da transformação do produto

## Auditoria e decisão

- A tela launcher original era `MainActivity` com Generate/Models/Exports/Settings.
- A engine AutoSub existente foi preservada: Whisper, Vosk/model manager, Gemma, fallback heurístico, `ShortsTranscriptAnalyzer`, `ShortsProjectStore`, `ShortsAutoFramer`, Media3 e serviços foreground/FFmpeg.
- A interface principal agora é a experiência CortaJá; as telas legadas não são mais abas primárias.

## UI implementada

- Home: CortaJá, tagline, Novo Projeto, Arquivo local, YouTube, IA local e projetos recentes.
- Navegação: Início, Projetos, Meus Cortes e Configurações.
- Novo Projeto: picker real de vídeo local, validação de URL pública YouTube, quantidade, duração, objetivo, formato, qualidade e análise.
- Análise: status de preparação, transcrição e entendimento; a fila real dispara Whisper e a análise Shorts.
- Resultados: review real existente com candidatos, ranking/nota, timestamps, motivo, assistir, editar, enquadramento e exportação.
- Preview/exportação: reutiliza o `ShortsReviewFragment` e o serviço de exportação real, mantendo os limites start/end do candidato.
- Modelos: removido da navegação principal; acessível em Configurações → IA Local → Modelos.

## pt-BR e identidade

- `app_name` é `CortaJá`.
- Labels de navegação e controles do review foram migrados para português.
- A superfície principal não usa AutoSub, Settings, Models, Exports, Generate, Add, Pending ou Processing.
- Textos herdados ainda existem em fragments internos de compatibilidade e devem ser tratados antes de expor essas rotas fora de Configurações.

## Pipeline conectado

`HomeFragment → NewProjectFragment → MainActivity picker → MainViewModel.addVideosToQueue → AutoSubTaskService/Whisper → MainViewModel.analyzeShorts → Gemma/fallback → ShortsProjectStore → ShortsReviewFragment → ShortsAutoFramer/FFmpeg export`.

## Testes

- `CortaJaDomainTest`: validação YouTube, labels pt-BR e duração/nota.
- `CortaJaUseCasesTest`: mapeamento de candidatos e preservação exata de start/end.
- `CortaJaHomeInstrumentedTest`: launcher/Home/tagline/CTA.
- JVM executada com sucesso:

```text
sh gradlew :app:testDebugUnitTest -Pandroid.aapt2FromMavenOverride=/data/data/com.termux/files/home/android-sdk/arm64-tools/aapt2 --console=plain
BUILD SUCCESSFUL
```

## Build e artefato

- Build local de recursos/Java passa com AAPT2 ARM64; a suíte JVM passou.
- O APK final ainda depende do workflow remoto por causa das bibliotecas nativas do Whisper no ambiente Termux ARM64.
- Artifact, APK, SHA256 e tamanho serão registrados após o run remoto passar.

## Bloqueios

- O download real de VOD YouTube ainda requer o downloader já previsto na base; nesta camada a URL é validada sem quebrar o fluxo local.
- Não há dispositivo Android conectado nesta sessão para executar o teste instrumentado.
