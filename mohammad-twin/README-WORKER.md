# Mohammad Twin Worker

This worker is the independent communication core for Mohammad Twin.

Flow:
Mohammad message -> inbox queue -> OpenAI model -> outbox -> dashboard/client.

Required environment:
OPENAI_API_KEY
OPENAI_MODEL (default: gpt-5)
POLL_SECONDS (default: 20)

The worker never receives bank passwords, CVV, OTPs, or card credentials.
Payment collection must use an authorized payment provider.
