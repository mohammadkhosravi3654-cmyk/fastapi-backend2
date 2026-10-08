import os
import json
import time
from datetime import datetime, timezone
from urllib.request import Request, urlopen

ROOT=os.path.dirname(os.path.abspath(__file__))
STATE=os.path.join(ROOT,"state.json")
QUEUE=os.path.join(ROOT,"message-queue.json")

SYSTEM="""You are Mohammad Twin, an AI operational assistant for Mohammad.
Act execution-first. Work only on lawful, transparent online activities.
Priorities: paid AI services, lead generation, commission opportunities, automation,
digital products, music, crafts, content, YouTube and Instagram.
Do not impersonate a human, fabricate identity, spam, deceive, access bank credentials,
withdraw money, or perform unauthorized financial actions.
When a user message arrives, decide the highest-value next action, produce a concise
action/result message, and preserve project separation. Never claim an action happened
unless the worker/tool actually completed it.
"""

def load(path, default):
    try:
        with open(path,"r",encoding="utf-8") as f:
            return json.load(f)
    except Exception:
        return default

def save(path,data):
    tmp=path+".tmp"
    with open(tmp,"w",encoding="utf-8") as f:
        json.dump(data,f,ensure_ascii=False,indent=2)
    os.replace(tmp,path)

def call_openai(messages):
    key=os.environ.get("OPENAI_API_KEY")
    if not key:
        return "OPENAI_API_KEY is not configured yet."
    body=json.dumps({
        "model":os.environ.get("OPENAI_MODEL","gpt-5"),
        "input":[{"role":"system","content":SYSTEM}]+messages
    }).encode("utf-8")
    req=Request("https://api.openai.com/v1/responses",data=body,
                headers={"Authorization":"Bearer "+key,"Content-Type":"application/json"})
    with urlopen(req,timeout=90) as r:
        data=json.loads(r.read().decode("utf-8"))
    return data.get("output_text","")

def run_once():
    q=load(QUEUE,{"inbox":[],"outbox":[]})
    if not q.get("inbox"):
        return False
    item=q["inbox"].pop(0)
    text=item.get("text","")
    answer=call_openai([{"role":"user","content":text}])
    q["outbox"].append({
        "id":item.get("id",str(time.time())),
        "created_at":datetime.now(timezone.utc).isoformat(),
        "reply":answer
    })
    save(QUEUE,q)
    state=load(STATE,{})
    state["last_worker_run"]=datetime.now(timezone.utc).isoformat()
    state["last_message_id"]=item.get("id")
    state["last_status"]="completed"
    save(STATE,state)
    return True

if __name__=="__main__":
    while True:
        try:
            run_once()
        except Exception as e:
            state=load(STATE,{})
            state["last_error"]=str(e)
            state["last_status"]="error"
            save(STATE,state)
        time.sleep(int(os.environ.get("POLL_SECONDS","20")))
