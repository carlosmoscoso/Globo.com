/**
 * main.js
 *
 * Funcionalidade: Votação BBB
 *
 * Cenário: Usuário clica no botão de votar
 *
 * 1) Se escolheu um candidato e respondeu o captcha
 * 2) Quando o serviço de votação confirmar o recebimento do POST
 * 3) Então atualize a interface do usuário com o resultado parcial
 */

function showpercentage(candidate, score = 0, polled) {
    document.getElementById(candidate).innerHTML =
            (polled > 0) ?
            "<h1>"+Math.round((score/polled)*100)+"%</h1>" :
            "<h1>0%</h1>";
}

function submitvote() {
    var candidate = $('input[name=candidate]:checked', '#Poll');
    
    if (candidate.val() == null) {
        alert('Escolha quem deve SAIR da casa.');
    } else if (grecaptcha.getResponse().length == 0) {
        alert('Favor resolver o captcha se for humano.');
    } else {
        var radio1 = document.getElementById('Radio1');
        radio1.checked = false;
        
        var radio2 = document.getElementById('Radio2');
        radio2.checked = false;
        
        var xhttp = new XMLHttpRequest();
        
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4) {
                if (200 <= this.status && this.status < 300) {
                    var votes = JSON.parse(this.responseText);
                    
                    var polled = Object.keys(votes)
                        .map(k => votes[k])
                        .reduce((a,b) => a+b, 0);
                    
                    showpercentage('Sister', votes['Sister'], polled);
                    showpercentage('Brother', votes['Brother'], polled);
                    
                    var footer = document.getElementById('Footer');
                    footer.innerHTML = "<p><b>Parab&eacute;ns!</b> Seu voto para <b>"+
                        candidate.val()+"</b> foi enviado com sucesso.</p>";
                    footer.setAttribute('class', 'center');
                    
                    var captcha = document.getElementById('Captcha')
                        .setAttribute('style', 'display: none;');
                    
                    radio1.disabled = true;
                    radio2.disabled = true;
                }
            }
        };
        
        xhttp.open("POST", "/poll", true);
        xhttp.send("candidate="+candidate.val()+"&token="+grecaptcha.getResponse());
    }
}
