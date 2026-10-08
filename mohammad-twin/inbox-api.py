from http.server import BaseHTTPRequestHandler, HTTPServer
import json, os, uuid

ROOT=os.path.dirname(os.path.abspath(__file__))
QUEUE=os.path.join(ROOT,"message-queue.json")

def load():
    try:
        with open(QUEUE,"r",encoding="utf-8") as f: return json.load(f)
    except: return {"inbox":[],"outbox":[]}

def save(x):
    with open(QUEUE,"w",encoding="utf-8") as f: json.dump(x,f,ensure_ascii=False,indent=2)

class H(BaseHTTPRequestHandler):
    def send_json(self,obj,code=200):
        b=json.dumps(obj,ensure_ascii=False).encode()
        self.send_response(code); self.send_header("Content-Type","application/json; charset=utf-8")
        self.send_header("Content-Length",str(len(b))); self.end_headers(); self.wfile.write(b)
    def do_GET(self):
        q=load()
        if self.path=="/messages":
            self.send_json({"messages":q.get("outbox",[])})
        else:
            self.send_json({"ok":True,"service":"mohammad-twin"})
    def do_POST(self):
        if self.path!="/message":
            self.send_json({"error":"not_found"},404); return
        n=int(self.headers.get("Content-Length","0"))
        body=json.loads(self.rfile.read(n).decode("utf-8"))
        text=body.get("text","").strip()
        if not text:
            self.send_json({"error":"text_required"},400); return
        q=load()
        q.setdefault("inbox",[]).append({"id":str(uuid.uuid4()),"text":text})
        save(q)
        self.send_json({"queued":True})
    def log_message(self,*args): pass

HTTPServer(("0.0.0.0",8765),H).serve_forever()
