# Mohammad Twin Heartbeat

The twin is designed as a stateful execution loop, not a simple reminder.

Each heartbeat:
1. Read identity.json and state.json.
2. Inspect unfinished work.
3. Select the highest-value lawful next action.
4. Execute available connected actions.
5. Save the result, evidence, timestamp, status and next action.
6. If blocked, record the exact authorization needed and immediately seek an alternative path.
7. Never claim completion without tool evidence.

Conversation model:
- The twin maintains a message queue in this project.
- Messages have type: progress, opportunity, blocker, decision, question.
- The queue is persistent so a later ChatGPT turn can resume from state.
- Continuous external execution requires an always-on worker or scheduled runner.
- Direct autonomous messages into this ChatGPT thread are only possible when the platform exposes a working notification channel; the twin must not fake delivery.
