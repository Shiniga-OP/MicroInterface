class Modulo {
    arvore(pacotes) {
        // pacotes: array de { caminho, classes[] }
        // retorna HTML da arvore de navegação
        return '';
    }

    classe(dados) {
        // dados: objeto completo da classe
        // retorna HTML da página de documentação
        return '';
    }
}

const FabricaDoc = (() => {
    let modulo = new Modulo();
    const classes = [];

    function formatar(dados) {
        classes.push(dados);
        FabricaDoc.renderizar();
    }

    function _montarArvore() {
        const nos = {};
        for(const cls of classes) {
            const partes = (cls.pacote || '').split('.');
            let atual = nos;
            let ultimoNo = null;
            for(const parte of partes) {
                if(!atual[parte]) atual[parte] = { _filhos: {}, _classes: [] };
                ultimoNo = atual[parte];
                atual = atual[parte]._filhos;
            }
            if(ultimoNo) ultimoNo._classes.push(cls);
        }
        return nos;
    }

    function _pacotesPlanos(nos, prefixo) {
        prefixo = prefixo || '';
        const lista = [];
        for(const chave of Object.keys(nos)) {
            if(chave === '_filhos' || chave === '_classes') continue;
            const no = nos[chave];
            const caminho = prefixo ? prefixo + '.' + chave : chave;
            lista.push({ caminho, classes: no._classes || [] });
            const sub = _pacotesPlanos(no._filhos || {}, caminho);
            for(const s of sub) lista.push(s);
        }
        return lista;
    }

    function _renderizar() {
        const arvore = _montarArvore();
        const pacotes = _pacotesPlanos(arvore, '');
        const estante = document.getElementById('fabrica-doc');
        if(!estante) return;

        estante.innerHTML =
            '<div id="fd-arvore">' + modulo.arvore(pacotes, classes) + '</div>' +
            '<div id="fd-conteudo"></div>';

        document.querySelectorAll('[data-fd-classe]').forEach(function(el) {
            el.addEventListener('click', function() {
                const nome = el.getAttribute('data-fd-classe');
                const pacote = el.getAttribute('data-fd-pacote');
                const cls = classes.find(function(c) {
                    return c.nome === nome && c.pacote === pacote;
                });
                if(cls) document.getElementById('fd-conteudo').innerHTML = modulo.classe(cls);
            });
        });
    }
    
    function carregarJSON(arquivo) {
        fetch(arquivo)
        .then(r => r.text())
        .then(dados => {
            const json = eval("("+dados+")");
            FabricaDoc.formatar(json);
            console.log(`✓ ${arquivo} carregado`);
        }).catch(e => console.error(`✗ ${arquivo}:`, e));
    }
    
    return {
        formatar,
        carregarJSON,
        get modulo() { return modulo; },
        set modulo(m) { modulo = m; },
        renderizar: _renderizar
    };
})();