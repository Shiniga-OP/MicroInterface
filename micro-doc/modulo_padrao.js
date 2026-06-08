class ModuloPadrao extends Modulo {
    constructor(titulo) {
        super();
        this.titulo = titulo;
    }
    _injetarEstilo() {
        if (document.getElementById('fd-estilo-padrao')) return;
        const s = document.createElement('style');
        s.id = 'fd-estilo-padrao';
        s.textContent = `
@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600&family=Syne:wght@400;700;800&display=swap');

*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

:root {
    --fundo: #0e0f11;
    --painel: #15171a;
    --borda: #2a2d33;
    --texto: #d4d8e0;
    --texto-fraco: #5a6070;
    --destaque: #e8c84a;
    --destaque2: #4ae8a8;
    --classe-cor: red;
    --kw-cor: #c084fc;
    --fonte-ui: 'Syne', sans-serif;
    --fonte-codigo: 'JetBrains Mono', monospace;
}

body {
    background: var(--fundo);
    color: var(--texto);
    font-family: var(--fonte-ui);
    display: flex;
    height: 100vh;
    overflow: hidden;
}

#fabrica-doc {
    display: flex;
    width: 100%;
    height: 100%;
}

#fd-arvore {
    width: 260px;
    min-width: 260px;
    background: var(--painel);
    border-right: 1px solid var(--borda);
    overflow-y: auto;
    display: flex;
    flex-direction: column;
}

#fd-conteudo {
    flex: 1;
    overflow-y: auto;
    padding: 40px 48px;
}

.fd-logo {
    padding: 20px 16px 16px;
    font-size: 13px;
    font-weight: 800;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: var(--destaque);
    border-bottom: 1px solid var(--borda);
}

.fd-lista-pacotes { list-style: none; padding: 8px 0; }
.fd-pacote { margin: 2px 0; }

.fd-pacote-nome {
    display: block;
    font-size: 11px;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: var(--texto-fraco);
    padding: 6px 0 2px;
}

.fd-lista-classes { list-style: none; }

.fd-classe-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 12px 6px 0;
    font-size: 13px;
    cursor: pointer;
    border-radius: 4px;
    color: var(--texto);
    transition: background 0.15s, color 0.15s;
}
.fd-classe-item:hover { background: #1f2228; color: var(--destaque); }

.fd-classe-icone {
    width: 18px;
    height: 18px;
    background: var(--classe-cor);
    color: var(--fundo);
    font-size: 10px;
    font-weight: 800;
    border-radius: 3px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    font-family: var(--fonte-codigo);
}

.fd-classe { max-width: 860px; }

.fd-cabecalho {
    border-bottom: 1px solid var(--borda);
    padding-bottom: 24px;
    margin-bottom: 32px;
}

.fd-tag-pacote {
    font-family: var(--fonte-codigo);
    font-size: 11px;
    color: var(--texto-fraco);
    letter-spacing: 0.04em;
}

.fd-nome-classe {
    font-size: 42px;
    font-weight: 800;
    color: #fff;
    line-height: 1.1;
    margin: 6px 0 8px;
}

.fd-herda {
    font-family: var(--fonte-codigo);
    font-size: 13px;
    color: var(--texto-fraco);
}
.fd-herda span { color: var(--destaque2); }

.fd-secao { margin-bottom: 36px; }

.fd-secao h2 {
    font-size: 11px;
    font-weight: 700;
    letter-spacing: 0.14em;
    text-transform: uppercase;
    color: var(--destaque);
    margin-bottom: 12px;
    padding-bottom: 6px;
    border-bottom: 1px solid var(--borda);
}

.fd-construtor {
    font-family: var(--fonte-codigo);
    font-size: 13px;
    padding: 8px 14px;
    background: #1a1c20;
    border-left: 2px solid var(--destaque);
    border-radius: 0 4px 4px 0;
    margin-bottom: 6px;
}

.fd-kw { color: var(--kw-cor); }

.fd-codigo {
    font-family: var(--fonte-codigo);
    font-size: 12px;
    line-height: 1.7;
    background: #1a1c20;
    border: 1px solid var(--borda);
    border-radius: 6px;
    padding: 16px 20px;
    overflow-x: auto;
    white-space: pre;
    color: var(--texto);
}

.fd-metodo {
    font-family: var(--fonte-codigo);
    font-size: 13px;
    padding: 10px 14px;
    background: #1a1c20;
    border: 1px solid var(--borda);
    border-radius: 4px;
    margin-bottom: 8px;
}

.fd-retorno { color: var(--destaque2); }
.fd-metodo-nome { color: #fff; font-weight: 600; }

.fd-descricao {
    font-family: var(--fonte-ui);
    font-size: 13px;
    color: var(--texto-fraco);
    margin-top: 6px;
}

::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: var(--borda); border-radius: 3px; }
        `;
        document.head.appendChild(s);
    }

    arvore(pacotes, classes) {
        this._injetarEstilo();
        let html = `<div class="fd-logo">${this.titulo}</div><ul class="fd-lista-pacotes">`;
        for(const p of pacotes) {
            const profundidade = p.caminho.split('.').length - 1;
            html += '<li class="fd-pacote" style="padding-left:' + (profundidade * 14 + 12) + 'px">';
            html += '<span class="fd-pacote-nome">◂ ' + p.caminho.split('.').pop() + '</span>';
            if(p.classes.length) {
                html += '<ul class="fd-lista-classes">';
                for(const cls of p.classes) {
                    html += '<li class="fd-classe-item" data-fd-classe="' + cls.nome + '" data-fd-pacote="' + cls.pacote + '">';
                    html += '<span class="fd-classe-icone">J</span>' + cls.nome;
                    html += '</li>';
                }
                html += '</ul>';
            }
            html += '</li>';
        }
        html += '</ul>';
        return html;
    }

    classe(dados) {
        let html = '<div class="fd-classe">';
        html += '<div class="fd-cabecalho">';
        html += '<span class="fd-tag-pacote">' + dados.pacote + '</span>';
        html += '<h1 class="fd-nome-classe">' + dados.nome + '</h1>';
        if(dados.herda) html += '<div class="fd-herda">extends <span>' + dados.herda + '</span></div>';
        html += '</div>';

        if(dados.construtores && dados.construtores.length) {
            html += '<section class="fd-secao">';
            html += '<h2>Construtores</h2>';
            for(const c of dados.construtores) {
                html += '<div class="fd-construtor"><span class="fd-kw">new</span> ' + dados.nome + '(' + c + ')</div>';
            }
            html += '</section>';
        }
        if(dados.exemplo) {
            html += '<section class="fd-secao">';
            html += '<h2>Exemplo</h2>';
            html += '<pre class="fd-codigo">' + dados.exemplo.trim() + '</pre>';
            html += '</section>';
        }
        if(dados.campos) {
            html += '<section class="fd-secao">';
            html += '<h2>Campos</h2>';
            html += '<pre class="fd-codigo">' + dados.campos.trim() + '</pre>';
            html += '</section>';
        }
        if(dados.metodos && dados.metodos.length) {
            html += '<section class="fd-secao">';
            html += '<h2>Métodos</h2>';
            for(const m of dados.metodos) {
                html += '<div class="fd-metodo">';
                html += '<span class="fd-retorno">' + m.retorno + '</span> ';
                html += '<span class="fd-metodo-nome">' + m.nome + '</span>';
                html += '(' + (m.args || '') + ')';
                if(m.faz) html += '<p class="fd-descricao">' + m.faz + '</p>';
                html += '</div>';
            }
            html += '</section>';
        }
        html += '</div>';
        return html;
    }
}