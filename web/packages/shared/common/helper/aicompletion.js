import api from '../service/api';
let timer
export function sendLLMRequest(data, position) {
    window.$APP_CONF.aisuggestion = false;
    clearTimeout(timer);
    if ((!data.segments.prefix && !data.segments.suffix)) {
        return Promise.resolve({
            items: []
        })
    }
    return new Promise((resolve, reject) => {
        timer = setTimeout(() => {
            return api.fetch(`/copilot/codecompletion`,
                { ...data },
                {
                    method: 'post',
                    timeout: 5000,
                    removeCache: true
                }).then((res) => {
                    if (res && res.choice) {
                        window.inlineCompletions = (res.choice || []).map(it => {
                            it.position = {
                                lineNumber: position.lineNumber,
                                column: position.column,
                            }
                            it.dbId = res.dbId
                            // it.text = it.text.replace(/^\s+/, '')
                            return it;
                        });
                        resolve({
                            items: window.inlineCompletions.map(it => {
                                return {
                                    "text": it.text,
                                }
                            })
                        })

                    } else {
                        resolve({
                            items: []
                        })
                    }
                }).catch(() => {
                    resolve({
                        items: []
                    })
                })
        }, 1000)

    })
}


export function sendAccteptRequest(data) {
    api.fetch(`/copilot/codeadoption`,
        { adopt: true, id:  data.dbId },
        {
            method: 'post',
        })
        .then(res => {
            console.log(res)
        })
        .catch(err => {
            console.log(err)
        })
}