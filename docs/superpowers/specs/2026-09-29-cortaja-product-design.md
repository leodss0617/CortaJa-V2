# CortaJá product experience

## Goal

Make the visible Android product CortaJá while retaining AutoSub's local media engine. The user-facing flow is Home → New Project → source/configuration → analysis → ranked semantic clips → preview → MP4 export.

## Architecture

`MainActivity` remains the host and owns only top-level navigation. A CortaJá presentation layer owns Home, project setup, analysis/results, My Cuts, and settings entry points. The existing Whisper, Gemma/fallback analyzer, auto-framer, subtitle generator, foreground media service, and `ShortsProjectStore` remain the implementation engines behind a CortaJá use-case façade.

The visible navigation is Início, Projetos, Meus Cortes, and Configurações. Legacy Generate/Models/Exports screens remain reachable only through internal compatibility routes or settings, never as primary tabs.

## Product rules

- All visible strings use Brazilian Portuguese resources.
- Candidate cards expose ranking, category, score, start, end, duration, and human-readable reason; raw energy/RMS/change values are diagnostic only.
- Local file selection is functional and independent of YouTube failure.
- Preview and export receive the selected candidate's exact `startMs`/`endMs`.
- Vertical export uses existing face tracking when available and smart center crop otherwise.
- Model management is nested under Configurações → IA Local → Modelos.
- Meus Cortes lists generated CortaJá video results, not subtitle files as the primary experience.

## Verification

Unit tests cover domain mapping, navigation labels, source validation, semantic candidate presentation, and exact preview/export time bounds. Instrumented smoke tests cover launcher/home, new-project navigation, local file picker, and the absence of legacy primary tabs.
