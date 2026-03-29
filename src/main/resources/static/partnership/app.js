(function () {
  const TOKEN_KEY = "campconnect_jwt";
  const ROLE_KEY = "campconnect_role";

  function contextPath() {
    const p = window.location.pathname;
    if (p.startsWith("/campconnect/")) return "/campconnect";
    return "";
  }

  const CTX = contextPath();
  const API = CTX + "/api/partnership";
  const AUTH = CTX + "/auth";

  const OFFER_STATUS = ["PROPOSED", "ACCEPTED", "REFUSED", "EXPIRED"];
  const CONTRACT_STATUS = ["IN_PROGRESS", "EXPIRED", "TERMINATED"];
  const INTERVIEW_DECISION = ["VALIDATE", "REFUSE", "TO_IMPROVE"];
  const INTERVIEW_MODE = ["IN_PERSON", "VIDEO", "PHONE"];
  const QUESTION_TYPE = ["MCQ", "OPEN", "NOTE", "YES_NO"];

  function $(id) {
    return document.getElementById(id);
  }

  function fmtDate(v) {
    if (v == null || v === "") return "";
    if (typeof v === "string") return v.length >= 10 ? v.slice(0, 10) : v;
    if (typeof v === "number") return new Date(v).toISOString().slice(0, 10);
    return String(v);
  }

  function fmtTime(v) {
    if (v == null || v === "") return "";
    if (typeof v === "string") return v;
    if (Array.isArray(v)) {
      const t = v;
      return [t[0], t[1] || 0, t[2] || 0]
        .map((x, i) => String(x).padStart(i === 0 ? 2 : 2, "0"))
        .join(":");
    }
    return String(v);
  }

  function getToken() {
    return localStorage.getItem(TOKEN_KEY);
  }

  function setSession(token, role) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(ROLE_KEY, role || "");
  }

  function clearSession() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(ROLE_KEY);
  }

  async function apiFetch(path, options) {
    const headers = Object.assign(
      { "Content-Type": "application/json" },
      options && options.headers ? options.headers : {}
    );
    const t = getToken();
    if (t) headers["Authorization"] = "Bearer " + t;
    const res = await fetch(path, Object.assign({}, options, { headers }));
    if (res.status === 401) {
      clearSession();
      showAuth();
      throw new Error("Non autorisé — reconnectez-vous.");
    }
    return res;
  }

  function showMsg(el, text, ok) {
    if (!el) return;
    el.className = "msg " + (ok ? "ok" : "err");
    el.textContent = text || "";
    el.classList.toggle("hidden", !text);
  }

  function showAuth() {
    $("auth-section").classList.remove("hidden");
    $("app-section").classList.add("hidden");
  }

  function showApp() {
    $("auth-section").classList.add("hidden");
    $("app-section").classList.remove("hidden");
    const role = localStorage.getItem(ROLE_KEY) || "";
    $("user-info").innerHTML =
      "Connecté — rôle <strong>" + escapeHtml(role) + "</strong>";
  }

  function escapeHtml(s) {
    if (s == null) return "";
    return String(s)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function optionList(values, selected) {
    return values
      .map(function (v) {
        return (
          '<option value="' +
          escapeHtml(v) +
          '"' +
          (v === selected ? " selected" : "") +
          ">" +
          escapeHtml(v) +
          "</option>"
        );
      })
      .join("");
  }

  const tabs = [
    { id: "offers", label: "Offres" },
    { id: "contrats", label: "Contrats" },
    { id: "interviews", label: "Entretiens" },
    { id: "meetings", label: "Réunions" },
    { id: "quizzes", label: "Quiz" },
    { id: "questions", label: "Questions" },
    { id: "reponses", label: "Réponses" },
  ];

  let activeTab = "offers";

  function renderNav() {
    const nav = $("nav-tabs");
    nav.innerHTML = tabs
      .map(function (t) {
        return (
          '<button type="button" data-tab="' +
          t.id +
          '" class="' +
          (t.id === activeTab ? "active" : "") +
          '">' +
          escapeHtml(t.label) +
          "</button>"
        );
      })
      .join("");
    nav.querySelectorAll("button[data-tab]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        activeTab = btn.getAttribute("data-tab");
        renderNav();
        renderPanel();
      });
    });
  }

  async function jsonOrText(res) {
    const ct = res.headers.get("content-type") || "";
    if (ct.includes("application/json")) return res.json();
    return res.text();
  }

  async function loadOffers() {
    const res = await apiFetch(API + "/offers");
    if (!res.ok) throw new Error((await res.text()) || res.statusText);
    return jsonOrText(res);
  }

  async function loadContrats() {
    const res = await apiFetch(API + "/contrats");
    if (!res.ok) throw new Error((await res.text()) || res.statusText);
    return jsonOrText(res);
  }

  async function loadInterviews() {
    const res = await apiFetch(API + "/interviews");
    if (!res.ok) throw new Error((await res.text()) || res.statusText);
    return jsonOrText(res);
  }

  async function loadMeetings() {
    const res = await apiFetch(API + "/meetings");
    if (!res.ok) throw new Error((await res.text()) || res.statusText);
    return jsonOrText(res);
  }

  async function loadQuizzes() {
    const res = await apiFetch(API + "/quizzes");
    if (!res.ok) throw new Error((await res.text()) || res.statusText);
    return jsonOrText(res);
  }

  async function loadQuestions() {
    const res = await apiFetch(API + "/questions");
    if (!res.ok) throw new Error((await res.text()) || res.statusText);
    return jsonOrText(res);
  }

  async function loadReponses() {
    const res = await apiFetch(API + "/reponses");
    if (!res.ok) throw new Error((await res.text()) || res.statusText);
    return jsonOrText(res);
  }

  function panelOffers(rows) {
    return (
      '<div class="panel" data-entity="offers">' +
      "<h2>Offres</h2>" +
      '<div class="grid-form">' +
      '<label>Titre <input type="text" id="of-title" /></label>' +
      '<label>Description <textarea id="of-desc"></textarea></label>' +
      '<label>Début <input type="date" id="of-start" /></label>' +
      '<label>Fin <input type="date" id="of-end" /></label>' +
      '<label>Prix <input type="number" step="0.01" id="of-price" /></label>' +
      '<label>Statut <select id="of-status">' +
      optionList(OFFER_STATUS, "PROPOSED") +
      "</select></label>" +
      "</div>" +
      '<button type="button" class="btn" id="of-add">Ajouter une offre</button>' +
      "<h3 style=\"margin-top:1.25rem;font-size:0.95rem\">Liste</h3>" +
      '<table class="data"><thead><tr>' +
      "<th>ID</th><th>Titre</th><th>Prix</th><th>Statut</th><th>Dates</th><th></th>" +
      "</tr></thead><tbody>" +
      rows
        .map(function (o) {
          return (
            "<tr><td>" +
            o.offerId +
            "</td><td>" +
            escapeHtml(o.title) +
            "</td><td>" +
            escapeHtml(o.price) +
            "</td><td>" +
            escapeHtml(o.status) +
            "</td><td>" +
            escapeHtml(fmtDate(o.startDate)) +
            " → " +
            escapeHtml(fmtDate(o.endDate)) +
            '</td><td><button type="button" class="btn-danger btn-sm" data-del="offer" data-id="' +
            o.offerId +
            '">Suppr.</button></td></tr>'
          );
        })
        .join("") +
      "</tbody></table></div>"
    );
  }

  function panelContrats(rows, offers) {
    const opts =
      '<option value="">—</option>' +
      offers
        .map(function (o) {
          return (
            '<option value="' +
            o.offerId +
            '">#' +
            o.offerId +
            " " +
            escapeHtml(o.title || "") +
            "</option>"
          );
        })
        .join("");
    return (
      '<div class="panel" data-entity="contrats">' +
      "<h2>Contrats</h2>" +
      '<div class="grid-form">' +
      '<label>Début <input type="date" id="ct-start" /></label>' +
      '<label>Fin <input type="date" id="ct-end" /></label>' +
      '<label>Commission <input type="number" step="0.01" id="ct-comm" /></label>' +
      '<label>Statut <select id="ct-status">' +
      optionList(CONTRACT_STATUS, "IN_PROGRESS") +
      "</select></label>" +
      '<label>Offre <select id="ct-offer">' +
      opts +
      "</select></label>" +
      "</div>" +
      '<button type="button" class="btn" id="ct-add">Ajouter un contrat</button>' +
      "<h3 style=\"margin-top:1.25rem;font-size:0.95rem\">Liste</h3>" +
      '<table class="data"><thead><tr>' +
      "<th>ID</th><th>Commission</th><th>Statut</th><th>Offre</th><th>Dates</th><th></th>" +
      "</tr></thead><tbody>" +
      rows
        .map(function (c) {
          return (
            "<tr><td>" +
            c.contractId +
            "</td><td>" +
            escapeHtml(c.commission) +
            "</td><td>" +
            escapeHtml(c.status) +
            "</td><td>" +
            escapeHtml(c.offerId) +
            "</td><td>" +
            escapeHtml(fmtDate(c.startDate)) +
            " → " +
            escapeHtml(fmtDate(c.endDate)) +
            '</td><td><button type="button" class="btn-danger btn-sm" data-del="contrat" data-id="' +
            c.contractId +
            '">Suppr.</button></td></tr>'
          );
        })
        .join("") +
      "</tbody></table></div>"
    );
  }

  function panelInterviews(rows) {
    return (
      '<div class="panel" data-entity="interviews">' +
      "<h2>Entretiens partenaires</h2>" +
      '<div class="grid-form">' +
      '<label>Date <input type="date" id="iv-date" /></label>' +
      '<label>Score global <input type="number" step="0.1" id="iv-score" /></label>' +
      '<label>Décision <select id="iv-dec">' +
      optionList(INTERVIEW_DECISION, "TO_IMPROVE") +
      "</select></label>" +
      '<label>ID utilisateur <input type="number" id="iv-user" placeholder="idUser" /></label>' +
      "</div>" +
      '<button type="button" class="btn" id="iv-add">Ajouter</button>' +
      "<h3 style=\"margin-top:1.25rem;font-size:0.95rem\">Liste</h3>" +
      '<table class="data"><thead><tr>' +
      "<th>ID</th><th>Date</th><th>Score</th><th>Décision</th><th>User</th><th></th>" +
      "</tr></thead><tbody>" +
      rows
        .map(function (x) {
          return (
            "<tr><td>" +
            x.interviewId +
            "</td><td>" +
            escapeHtml(fmtDate(x.interviewDate)) +
            "</td><td>" +
            escapeHtml(x.globalScore) +
            "</td><td>" +
            escapeHtml(x.decision) +
            "</td><td>" +
            escapeHtml(x.userId) +
            '</td><td><button type="button" class="btn-danger btn-sm" data-del="interview" data-id="' +
            x.interviewId +
            '">Suppr.</button></td></tr>'
          );
        })
        .join("") +
      "</tbody></table></div>"
    );
  }

  function panelMeetings(rows, interviews) {
    const opts =
      '<option value="">—</option>' +
      interviews
        .map(function (x) {
          return (
            '<option value="' +
            x.interviewId +
            '">#' +
            x.interviewId +
            " (" +
            escapeHtml(fmtDate(x.interviewDate)) +
            ")</option>"
          );
        })
        .join("");
    return (
      '<div class="panel" data-entity="meetings">' +
      "<h2>Réunions d’entretien</h2>" +
      '<div class="grid-form">' +
      '<label>Date <input type="date" id="mt-date" /></label>' +
      '<label>Début (HH:MM) <input type="time" id="mt-start" /></label>' +
      '<label>Fin (HH:MM) <input type="time" id="mt-end" /></label>' +
      '<label>Mode <select id="mt-mode">' +
      optionList(INTERVIEW_MODE, "VIDEO") +
      "</select></label>" +
      '<label>Lieu <input type="text" id="mt-loc" /></label>' +
      '<label>Compte rendu <textarea id="mt-report"></textarea></label>' +
      '<label>Entretien <select id="mt-iv">' +
      opts +
      "</select></label>" +
      "</div>" +
      '<button type="button" class="btn" id="mt-add">Ajouter</button>' +
      "<h3 style=\"margin-top:1.25rem;font-size:0.95rem\">Liste</h3>" +
      '<table class="data"><thead><tr>' +
      "<th>ID</th><th>Date</th><th>Horaires</th><th>Mode</th><th>Entretien</th><th></th>" +
      "</tr></thead><tbody>" +
      rows
        .map(function (m) {
          return (
            "<tr><td>" +
            m.meetingId +
            "</td><td>" +
            escapeHtml(fmtDate(m.meetingDate)) +
            "</td><td>" +
            escapeHtml(fmtTime(m.startTime)) +
            " – " +
            escapeHtml(fmtTime(m.endTime)) +
            "</td><td>" +
            escapeHtml(m.mode) +
            "</td><td>" +
            escapeHtml(m.interviewId) +
            '</td><td><button type="button" class="btn-danger btn-sm" data-del="meeting" data-id="' +
            m.meetingId +
            '">Suppr.</button></td></tr>'
          );
        })
        .join("") +
      "</tbody></table></div>"
    );
  }

  function panelQuizzes(rows) {
    return (
      '<div class="panel" data-entity="quizzes">' +
      "<h2>Quiz</h2>" +
      '<div class="grid-form">' +
      '<label>Titre <input type="text" id="qz-title" /></label>' +
      '<label>Score max <input type="number" step="0.1" id="qz-max" /></label>' +
      '<label>ID utilisateur <input type="number" id="qz-user" /></label>' +
      "</div>" +
      '<button type="button" class="btn" id="qz-add">Ajouter</button>' +
      "<h3 style=\"margin-top:1.25rem;font-size:0.95rem\">Liste</h3>" +
      '<table class="data"><thead><tr>' +
      "<th>ID</th><th>Titre</th><th>Max</th><th>User</th><th></th>" +
      "</tr></thead><tbody>" +
      rows
        .map(function (q) {
          return (
            "<tr><td>" +
            q.quizId +
            "</td><td>" +
            escapeHtml(q.title) +
            "</td><td>" +
            escapeHtml(q.maxScore) +
            "</td><td>" +
            escapeHtml(q.userId) +
            '</td><td><button type="button" class="btn-danger btn-sm" data-del="quiz" data-id="' +
            q.quizId +
            '">Suppr.</button></td></tr>'
          );
        })
        .join("") +
      "</tbody></table></div>"
    );
  }

  function panelQuestions(rows, quizzes) {
    const opts =
      '<option value="">—</option>' +
      quizzes
        .map(function (q) {
          return (
            '<option value="' +
            q.quizId +
            '">#' +
            q.quizId +
            " " +
            escapeHtml(q.title || "") +
            "</option>"
          );
        })
        .join("");
    return (
      '<div class="panel" data-entity="questions">' +
      "<h2>Questions</h2>" +
      '<div class="grid-form">' +
      '<label>Libellé <input type="text" id="pq-label" /></label>' +
      '<label>Type <select id="pq-type">' +
      optionList(QUESTION_TYPE, "OPEN") +
      "</select></label>" +
      '<label>Pondération <input type="number" step="0.1" id="pq-w" /></label>' +
      '<label>Quiz <select id="pq-quiz">' +
      opts +
      "</select></label>" +
      "</div>" +
      '<button type="button" class="btn" id="pq-add">Ajouter</button>' +
      "<h3 style=\"margin-top:1.25rem;font-size:0.95rem\">Liste</h3>" +
      '<table class="data"><thead><tr>' +
      "<th>ID</th><th>Libellé</th><th>Type</th><th>Poids</th><th>Quiz</th><th></th>" +
      "</tr></thead><tbody>" +
      rows
        .map(function (q) {
          return (
            "<tr><td>" +
            q.questionId +
            "</td><td>" +
            escapeHtml(q.label) +
            "</td><td>" +
            escapeHtml(q.type) +
            "</td><td>" +
            escapeHtml(q.weight) +
            "</td><td>" +
            escapeHtml(q.quizId) +
            '</td><td><button type="button" class="btn-danger btn-sm" data-del="question" data-id="' +
            q.questionId +
            '">Suppr.</button></td></tr>'
          );
        })
        .join("") +
      "</tbody></table></div>"
    );
  }

  function panelReponses(rows, questions) {
    const opts =
      '<option value="">—</option>' +
      questions
        .map(function (q) {
          return (
            '<option value="' +
            q.questionId +
            '">#' +
            q.questionId +
            " " +
            escapeHtml((q.label || "").slice(0, 40)) +
            "</option>"
          );
        })
        .join("");
    return (
      '<div class="panel" data-entity="reponses">' +
      "<h2>Réponses au quiz</h2>" +
      '<div class="grid-form">' +
      '<label>Valeur <textarea id="rp-val"></textarea></label>' +
      '<label>Note <input type="number" step="0.1" id="rp-grade" /></label>' +
      '<label>Question <select id="rp-q">' +
      opts +
      "</select></label>" +
      "</div>" +
      '<button type="button" class="btn" id="rp-add">Ajouter</button>' +
      "<h3 style=\"margin-top:1.25rem;font-size:0.95rem\">Liste</h3>" +
      '<table class="data"><thead><tr>' +
      "<th>ID</th><th>Valeur</th><th>Note</th><th>Question</th><th></th>" +
      "</tr></thead><tbody>" +
      rows
        .map(function (r) {
          return (
            "<tr><td>" +
            r.responseId +
            "</td><td>" +
            escapeHtml((r.value || "").slice(0, 80)) +
            "</td><td>" +
            escapeHtml(r.grade) +
            "</td><td>" +
            escapeHtml(r.questionId) +
            '</td><td><button type="button" class="btn-danger btn-sm" data-del="reponse" data-id="' +
            r.responseId +
            '">Suppr.</button></td></tr>'
          );
        })
        .join("") +
      "</tbody></table></div>"
    );
  }

  async function renderPanel() {
    const root = $("panel-root");
    const msg = $("app-msg");
    showMsg(msg, "", true);
    root.innerHTML = "<p style=\"color:var(--muted)\">Chargement…</p>";
    try {
      const offers = await loadOffers();
      const contrats = await loadContrats();
      const interviews = await loadInterviews();
      const meetings = await loadMeetings();
      const quizzes = await loadQuizzes();
      const questions = await loadQuestions();
      const reponses = await loadReponses();

      let html = "";
      if (activeTab === "offers") html = panelOffers(offers);
      else if (activeTab === "contrats") html = panelContrats(contrats, offers);
      else if (activeTab === "interviews") html = panelInterviews(interviews);
      else if (activeTab === "meetings") html = panelMeetings(meetings, interviews);
      else if (activeTab === "quizzes") html = panelQuizzes(quizzes);
      else if (activeTab === "questions") html = panelQuestions(questions, quizzes);
      else if (activeTab === "reponses") html = panelReponses(reponses, questions);

      root.innerHTML = html;
      wireActions();
    } catch (e) {
      root.innerHTML = "";
      showMsg(msg, e.message || String(e), false);
    }
  }

  function timeToBackend(inputTime) {
    if (!inputTime) return null;
    return inputTime.length === 5 ? inputTime + ":00" : inputTime;
  }

  function wireActions() {
    const msg = $("app-msg");

    const root = $("panel-root");
    root.querySelectorAll("[data-del]").forEach(function (btn) {
      btn.addEventListener("click", async function () {
        const kind = btn.getAttribute("data-del");
        const id = btn.getAttribute("data-id");
        let path = "";
        if (kind === "offer") path = API + "/offers/" + id;
        else if (kind === "contrat") path = API + "/contrats/" + id;
        else if (kind === "interview") path = API + "/interviews/" + id;
        else if (kind === "meeting") path = API + "/meetings/" + id;
        else if (kind === "quiz") path = API + "/quizzes/" + id;
        else if (kind === "question") path = API + "/questions/" + id;
        else if (kind === "reponse") path = API + "/reponses/" + id;
        if (!path) return;
        if (!confirm("Supprimer cet élément ?")) return;
        try {
          const res = await apiFetch(path, { method: "DELETE" });
          if (!res.ok) throw new Error(await res.text());
          showMsg(msg, "Supprimé.", true);
          renderPanel();
        } catch (e) {
          showMsg(msg, e.message || String(e), false);
        }
      });
    });

    const add = function (id, fn) {
      const el = $(id);
      if (el) el.addEventListener("click", fn);
    };

    add("of-add", async function () {
      const body = {
        title: $("of-title").value,
        description: $("of-desc").value,
        startDate: $("of-start").value || null,
        endDate: $("of-end").value || null,
        price: $("of-price").value ? parseFloat($("of-price").value) : null,
        status: $("of-status").value,
      };
      try {
        const res = await apiFetch(API + "/offers", {
          method: "POST",
          body: JSON.stringify(body),
        });
        if (!res.ok) throw new Error(await res.text());
        showMsg(msg, "Offre créée.", true);
        renderPanel();
      } catch (e) {
        showMsg(msg, e.message || String(e), false);
      }
    });

    add("ct-add", async function () {
      const oid = $("ct-offer").value;
      const body = {
        startDate: $("ct-start").value || null,
        endDate: $("ct-end").value || null,
        commission: $("ct-comm").value ? parseFloat($("ct-comm").value) : null,
        status: $("ct-status").value,
        offerId: oid ? parseInt(oid, 10) : null,
      };
      try {
        const res = await apiFetch(API + "/contrats", {
          method: "POST",
          body: JSON.stringify(body),
        });
        if (!res.ok) throw new Error(await res.text());
        showMsg(msg, "Contrat créé.", true);
        renderPanel();
      } catch (e) {
        showMsg(msg, e.message || String(e), false);
      }
    });

    add("iv-add", async function () {
      const uid = $("iv-user").value;
      const body = {
        interviewDate: $("iv-date").value || null,
        globalScore: $("iv-score").value ? parseFloat($("iv-score").value) : null,
        decision: $("iv-dec").value,
        userId: uid ? parseInt(uid, 10) : null,
      };
      try {
        const res = await apiFetch(API + "/interviews", {
          method: "POST",
          body: JSON.stringify(body),
        });
        if (!res.ok) throw new Error(await res.text());
        showMsg(msg, "Entretien créé.", true);
        renderPanel();
      } catch (e) {
        showMsg(msg, e.message || String(e), false);
      }
    });

    add("mt-add", async function () {
      const iid = $("mt-iv").value;
      const body = {
        meetingDate: $("mt-date").value || null,
        startTime: timeToBackend($("mt-start").value),
        endTime: timeToBackend($("mt-end").value),
        mode: $("mt-mode").value,
        location: $("mt-loc").value,
        report: $("mt-report").value,
        interviewId: iid ? parseInt(iid, 10) : null,
      };
      try {
        const res = await apiFetch(API + "/meetings", {
          method: "POST",
          body: JSON.stringify(body),
        });
        if (!res.ok) throw new Error(await res.text());
        showMsg(msg, "Réunion créée.", true);
        renderPanel();
      } catch (e) {
        showMsg(msg, e.message || String(e), false);
      }
    });

    add("qz-add", async function () {
      const uid = $("qz-user").value;
      const body = {
        title: $("qz-title").value,
        maxScore: $("qz-max").value ? parseFloat($("qz-max").value) : null,
        userId: uid ? parseInt(uid, 10) : null,
      };
      try {
        const res = await apiFetch(API + "/quizzes", {
          method: "POST",
          body: JSON.stringify(body),
        });
        if (!res.ok) throw new Error(await res.text());
        showMsg(msg, "Quiz créé.", true);
        renderPanel();
      } catch (e) {
        showMsg(msg, e.message || String(e), false);
      }
    });

    add("pq-add", async function () {
      const qid = $("pq-quiz").value;
      const body = {
        label: $("pq-label").value,
        type: $("pq-type").value,
        weight: $("pq-w").value ? parseFloat($("pq-w").value) : null,
        quizId: qid ? parseInt(qid, 10) : null,
      };
      try {
        const res = await apiFetch(API + "/questions", {
          method: "POST",
          body: JSON.stringify(body),
        });
        if (!res.ok) throw new Error(await res.text());
        showMsg(msg, "Question créée.", true);
        renderPanel();
      } catch (e) {
        showMsg(msg, e.message || String(e), false);
      }
    });

    add("rp-add", async function () {
      const qid = $("rp-q").value;
      const body = {
        value: $("rp-val").value,
        grade: $("rp-grade").value ? parseFloat($("rp-grade").value) : null,
        questionId: qid ? parseInt(qid, 10) : null,
      };
      try {
        const res = await apiFetch(API + "/reponses", {
          method: "POST",
          body: JSON.stringify(body),
        });
        if (!res.ok) throw new Error(await res.text());
        showMsg(msg, "Réponse créée.", true);
        renderPanel();
      } catch (e) {
        showMsg(msg, e.message || String(e), false);
      }
    });
  }

  async function doLogin() {
    const email = $("login-email").value;
    const password = $("login-password").value;
    const box = $("auth-msg");
    showMsg(box, "", true);
    try {
      const res = await fetch(AUTH + "/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email, password: password }),
      });
      const data = await jsonOrText(res);
      if (!res.ok) {
        throw new Error(
          typeof data === "string" ? data : JSON.stringify(data)
        );
      }
      setSession(data.token, data.role);
      showApp();
      renderNav();
      renderPanel();
    } catch (e) {
      showMsg(box, e.message || String(e), false);
    }
  }

  async function doRegister() {
    const box = $("auth-msg");
    showMsg(box, "", true);
    const body = {
      firstName: $("reg-first").value,
      lastName: $("reg-last").value,
      email: $("reg-email").value,
      password: $("reg-pass").value,
      phone: $("reg-phone").value,
      role: "PARTNER",
    };
    try {
      const res = await fetch(AUTH + "/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });
      const text = await res.text();
      if (!res.ok) throw new Error(text);
      showMsg(box, text || "Compte créé — vous pouvez vous connecter.", true);
    } catch (e) {
      showMsg(box, e.message || String(e), false);
    }
  }

  function init() {
    $("btn-login").addEventListener("click", doLogin);
    $("btn-register").addEventListener("click", doRegister);
    $("btn-logout").addEventListener("click", function () {
      clearSession();
      showAuth();
    });

    if (getToken()) {
      showApp();
      renderNav();
      renderPanel();
    } else {
      showAuth();
    }
  }

  init();
})();
