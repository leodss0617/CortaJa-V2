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
- Artifact: CortaJa-V2-TESTE; APK: /storage/emulated/0/Download/CortaJa-V2-PTBR.apk; tamanho: 175461189 bytes; SHA256: 4f91bd536c10483523cacc82cbbf7d37aeaf09c21091de320bb9d854a2e800e6.

## Bloqueios

- O download real de VOD YouTube ainda requer o downloader já previsto na base; nesta camada a URL é validada sem quebrar o fluxo local.
- Não há dispositivo Android conectado nesta sessão para executar o teste instrumentado.
## Fluxo automático de links — 2026-09-29
- PublicVideoSourceResolver com YouTubeProvider, TwitchProvider e KickProvider; entrada única para vídeo, VOD e live.
- Mídia resolvida entra no mesmo addVideosToQueue → Whisper → Shorts/Gemma/fallback → resultados do arquivo local.
- YouTube Player público sem autenticação; Twitch Usher público; Kick API/playback HLS público.
- Smoke: YouTube oEmbed 200 e Player WEB 200/streamingData; Kick API 200/playback_url; Twitch Usher 403 no canal/ID testado, tratado como bloqueio público recuperável.
- Segurança: sem DRM bypass, CAPTCHA, cookies, login ou anti-bot bypass. Testes cobrem YouTube watch/short/live, Twitch VOD/live, Kick VOD/live e URL inválida.
- LINKS artifact run 36608150790: /storage/emulated/0/Download/CortaJa-V2-LINKS.apk, 175477573 bytes, SHA256 c8ccb5bd783535c9cc3c76190695744ddfd3e01bf0b8492ddda38ce38a55fd4c.
## yt-dlp e VOD longo — 2026-09-29
- Dependências integradas: youtubedl-android 0.18.1 (library + ffmpeg), inicialização no App e atualização STABLE com retry único e fallback NIGHTLY.
- `YtDlpSourceResolver` é a primeira tentativa para YouTube, Twitch e Kick; `PublicVideoSourceResolver` permanece apenas como fallback nativo.
- VODs são planejados em blocos de 15 minutos (limites internos 10–20), com yt-dlp `--download-sections`, áudio primeiro, checkpoint SQLite por projeto/bloco e retomada no próximo bloco seguro.
- `LongVodForegroundRunner` usa o AutoSubTaskService foreground, envia um bloco por vez ao Whisper, libera o arquivo temporário e dispara Shorts/Gemma/fallback somente após a transcrição.
- Testes novos: durações 5min/30min/1h/3h/6h/10h, clamp de bloco, resume, dedup/ranking global e retry do resolvedor yt-dlp.
- Licença: youtubedl-android GPL-3.0; obrigações e links registrados em `docs/NOTICE-YTDLP-ANDROID.md`.
- Smoke externo de URLs públicas não foi convertido em teste determinístico: plataformas podem exigir disponibilidade regional, rate limit ou conteúdo público específico.
- Remote build 36623518553: PASS (unit tests, instrumentation compilation, assemble debug).
- Artifact final: `/storage/emulated/0/Download/CortaJa-V2-10H.apk`, 173717744 bytes, SHA256 `8c13be6431b19ad0e56edf1ace92e23db95d839b37461e13a306538a77ce308d`.
