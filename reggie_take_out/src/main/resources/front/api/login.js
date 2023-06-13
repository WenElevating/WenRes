function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

function sendVerCode(data){
    return $axios({
        'url': '/user/emailCode',
        'method': 'post',
        data
    })
}