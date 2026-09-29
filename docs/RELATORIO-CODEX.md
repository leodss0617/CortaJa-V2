# CortaJá V2 — Relatório Codex

## Base utilizada

- Repositório: `https://github.com/Serkali-sudo/auto-subtitle-generator`
- Commit clonado: `a5ae6abe8b13a7d3afc1a5f3c9b9e497be3004d6`
- Projeto separado: `/storage/emulated/0/Download/CortaJa-V2`
- O projeto antigo `/storage/emulated/0/Download/CortaJa-IA-Workspace` não foi modificado.

## Arquitetura aproveitada

- Whisper.cpp Android/JNI e modelo bundled.
- `SubtitleGenerator` com timestamps word-level, VAD e exportação FFmpeg.
- Media3 para reprodução.
- Shorts/Gemma LiteRT-LM, persistência de projeto e revisão de candidatos.
- Crop vertical e face framing existentes, com smart center crop como fallback do renderer.
- Serviço foreground para trabalho longo e liberação explícita do modelo de fala antes da análise Shorts.

## Alterações desta etapa

- Adicionado `LocalShortsFallbackAnalyzer`, sem dependência Android, para gerar candidatos mesmo sem Gemma.
- O serviço não bloqueia mais análise por Android < 12 ou Gemma ausente.
- Falhas de Gemma/LiteRT/JNI e respostas vazias tentam fallback local; o erro permanece recuperável na UI.
- Fallback usa boundaries de frase, sinais lexicais de hook/setup/payoff, pontuação determinística, duração adaptativa e deduplicação por sobreposição temporal + similaridade textual.
- Adicionados testes unitários para thought completo, ranking hook/payoff e dedup.
- `local.properties` é apenas configuração local do ambiente e não deve ser versionado.

## Testes

Comando executado:

```text
sh gradlew :app:testDebugUnitTest -Pandroid.aapt2FromMavenOverride=/data/data/com.termux/files/home/android-sdk/arm64-tools/aapt2 --console=plain
```

Resultado: **BUILD SUCCESSFUL**.

Inclui os testes existentes de Shorts, mídia, captions e timings, mais `LocalShortsFallbackAnalyzerTest`.

## Build remoto oficial

O desenvolvimento foi feito no Termux ARM64. O build nativo local foi bloqueado porque o NDK acessível ao Gradle contém toolchain host x86_64, incompatível com o host ARM64; o Clang alternativo do Termux não possui os runtimes Android do NDK. Isso não é erro funcional do aplicativo nem motivo para reescrever/remover o Whisper.

O build oficial passa a ser feito por [`.github/workflows/build-android.yml`](../.github/workflows/build-android.yml) em `ubuntu-latest` x86_64, com JDK Temurin 17, Gradle 8.13, AGP 8.13.2, SDK 36, NDK 27.0.12077973 e CMake 3.22.1. O workflow executa `testDebugUnitTest` e `assembleDebug`, exige APK real, gera `build-info.txt` e publica o artifact `CortaJa-V2-TESTE`.

A configuração mantém prioridade `arm64-v8a` e também preserva `armeabi-v7a`, `x86` e `x86_64` conforme a base.

### Ciclo autônomo de CI

- Run `36503729013`: falhou em `Set up Android SDK`.
- Causa raiz: `android-actions/setup-android@v3` usou o padrão `packages: tools platform-tools`; o runner atual não fornece mais o pacote legado `tools`, e `sdkmanager tools` terminou com `Failed to find package 'tools'`.
- Correção aplicada: limitar o setup inicial a `packages: platform-tools`; plataformas, Build Tools, NDK e CMake continuam sendo instalados explicitamente no passo seguinte.
- Commit da correção: `3f6e73c`.
- Run `36504899493`: SDK, NDK, CMake e testes unitários passaram; falhou em `Compile Android instrumentation tests`.
- Causa raiz: `compileDebugAndroidTest` é ambígua no Gradle 8.13 e não seleciona uma tarefa única.
- Correção seguinte: usar a tarefa agregadora explícita `:app:compileDebugAndroidTestSources`.
- O run bem-sucedido gerou os quatro APKs ABI e o artifact ficou com 454228644 bytes; para manter prioridade ARM64 e permitir o download automático, o artifact seguinte publicará somente o APK `arm64-v8a`.

### Resultado final do build remoto

- Runs: `36503729013` (SDK falhou), `36504899493` (tarefa Android test ambígua), `36505252181` (SUCCESS, quatro APKs) e `36507186622` (SUCCESS, artifact ARM64).
- Commits: `3f6e73c` (`build: fix android sdk setup`), `cf11e7c` (`build: target android test sources explicitly`) e `c2ff208` (`build: publish arm64 apk artifact`).
- `ubuntu-latest`, JDK 17, compile/target SDK 36, NDK `27.0.12077973`, CMake `3.22.1`.
- Unit tests: PASS (`testDebugUnitTest`). Android test compile: PASS (`:app:compileDebugAndroidTestSources`). Assemble debug: PASS.
- Artifact: `CortaJa-V2-TESTE`, run `36507186622`, 116670249 bytes.
- APK: `/storage/emulated/0/Download/CortaJa-V2-TESTE.apk`.
- APK size: `175455109` bytes.
- APK SHA-256: `d72b69b5d395cb4257604ac3436273abc84d21f78afdfdb9c2fd83b03ec672f3`.

## Build/APK

O build remoto foi preparado; a execução do APK depende do GitHub Actions deste repositório. Localmente, `assembleDebug` foi tentado três vezes. O build Java/recursos passa, mas o link nativo Whisper falha neste ambiente ARM64 porque o NDK instalado contém toolchain host x86_64 e o fallback Clang do Termux não possui os runtimes Android do NDK (`crtbegin_dynamic.o`, `libatomic`, `libunwind`, `libclang_rt.builtins.a`). Portanto o APK solicitado ainda não foi gerado, copiado nem hasheado.

## Limitações conhecidas

- Não houve teste Android real nesta sessão.
- APK bloqueado pela toolchain nativa descrita acima; é necessário executar o build em host com NDK Android compatível (ou instalar um NDK ARM64 completo com sysroot/runtimes).
- Whisper JNI, Gemma LiteRT, MediaCodec, FFmpeg, YouTube VOD, primeiro frame/frame intermediário e compatibilidade com Galeria ainda precisam de smoke test em aparelho.
- Download YouTube VOD e diagnóstico visual continuam dependentes do fluxo da base; live permanece fora do escopo.
- A validação final de MP4 deve confirmar arquivo, tamanho, duração, tracks, dimensões, rotação e decodificação de frames.

## Próximo passo obrigatório

Instalar o APK em aparelho Android, testar primeiro vídeo local de 2–5 minutos, depois YouTube VOD de 5–10 minutos, e guardar logs/resultado de cada etapa sem tratar falha nativa como crash aceitável.
