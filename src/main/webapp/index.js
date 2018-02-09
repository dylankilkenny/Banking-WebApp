/* global $*/
$(document).on('click', '#signup', function(){
    event.preventDefault();
    var name = $("#name").val();
    var email = $("#email").val();
    var age = parseInt($("#age").val());
    var address = $("#address").val();
    var pin = parseInt($("#pin").val());
    var user = {
        "name": name,
        "email": email,
        "age": age,
        "address": address,
        "pin": pin
    }
    console.log(user);
   
    $.ajax({
      type: 'POST',
      data: JSON.stringify(user),
      contentType: 'application/json',
      headers: { "API_KEY":"VALID_KEY"},
      url: 'http://localhost:8080/api/users',
      success: function (data) {
        console.log(data)
        var xmlText = new XMLSerializer().serializeToString(data);
        console.log(xmlText)
        var xmlDoc = $.parseXML( xmlText ),
        $xml = $( xmlDoc ),
        $apikey = $xml.find("api_key");
        var $userID = $xml.find("user").children("id");
        console.log($userID.text());

        localStorage.setItem("apikey", $apikey.text());
        localStorage.setItem("id", $userID.text());
        window.location = 'bank.html?id=' + $userID.text();

      }
    });
});

$(document).on('click', '#login', function(){
  event.preventDefault();
  var email = $("#email").val();
  var pin = parseInt($("#pin").val());
 
  $.ajax({
    type: 'POST',
    contentType: 'application/json',
    url: 'http://localhost:8080/api/users/login?email='+email+'&pin='+pin,
    success: function (data) {
      console.log(data)
      var xmlText = new XMLSerializer().serializeToString(data);
      console.log(xmlText)
      var xmlDoc = $.parseXML( xmlText ),
      $xml = $( xmlDoc ),
      $apikey = $xml.find("api_key");
      var $userID = $xml.find("user").children("id");

      localStorage.setItem("apikey", $apikey.text());
      localStorage.setItem("id", $userID.text());
      window.location = 'bank.html?id=' + $userID.text();

    }
  });
});

$(document).on('click', '#transaction', function(){
  event.preventDefault();
  var accountID = $(this).closest('tr').find('#accountID').text();
 
  $.ajax({
    type: 'GET',
    contentType: 'application/json',
    url: 'http://localhost:8080/api/account/'+accountID+'/transactions',
    success: function (data) {
      console.log(data)
      var xmlText = new XMLSerializer().serializeToString(data);
      console.log(xmlText)
      var xmlDoc = $.parseXML( xmlText ),
      $xml = $( xmlDoc );
      $('#transBody').empty();
      $xml.find('transaction').each(function(index){
        var id = $(this).find("id").text(),
        amount = $(this).find("amount").text(),
        type = $(this).find("type").text();
        $('#TransTable').css("display", "block");
        
        $('#transBody').append('<tr><td id=accountID>'+id+'</td><td>'+type+'</td><td>€'+amount+'</td></tr>');
      }); 
    }
  });
});
$(document).on('click', '#deposit', function(){
  event.preventDefault();
  var accountID = $(this).closest('tr').find('#accountID').text();
  var amount = parseFloat(prompt("Enter amount to deposit:"));
  $.ajax({
    type: 'POST',
    contentType: 'application/json',
    url: 'http://localhost:8080/api/account/'+accountID+'/deposit?amount='+amount,
    statusCode: {
      201: function (data) {
          location.reload();
      }
  },
    error: function(data){
      console.log(data);
    }
  });
});

$(document).on('click', '#withdraw', function(){
  event.preventDefault();
  var accountID = $(this).closest('tr').find('#accountID').text();
  var amount = parseFloat(prompt("Enter amount to withdraw:"));
  $.ajax({
    type: 'POST',
    contentType: 'application/json',
    url: 'http://localhost:8080/api/account/'+accountID+'/withdraw?amount='+amount,
    statusCode: {
      200: function (data) {
          location.reload();
      }
  },
    error: function(data){
      console.log(data);
    }
  });
});

$(document).on('click', '#viewAll', function(){
  event.preventDefault();
  var apikey = localStorage.getItem("apikey");
  var userID = localStorage.getItem("id");
    $.ajax({
    type: 'GET',
    contentType: 'application/json',
    headers: { "API_TOKEN": apikey},
    url: 'http://localhost:8080/api/users/'+userID+'/transactions',
    success: function(data){
      console.log(data)
      var xmlText = new XMLSerializer().serializeToString(data);
      console.log(xmlText)
      var xmlDoc = $.parseXML( xmlText ),
      $xml = $( xmlDoc );
      $('#transBody').empty();
      $xml.find('transaction').each(function(index){
        var id = $(this).find("id").text(),
        amount = $(this).find("amount").text(),
        type = $(this).find("type").text();
        $('#TransTable').css("display", "block");
        
        $('#transBody').append('<tr><td id=accountID>'+id+'</td><td>'+type+'</td><td>€'+amount+'</td></tr>');
      }); 
    }
  });
});

$(document).on('click', '#transfer', function(){
  event.preventDefault();
  var accountID = $(this).closest('tr').find('#accNum').text();
  var account = prompt("Enter account number to transfer to:");
  var amount = prompt("Enter the amount:");
  if (account == null || amount == null) {
    console.log("null")
    return; //break out of the function early
  }
  $.ajax({
    type: 'POST',
    contentType: 'application/json',
    url: 'http://localhost:8080/api/account/'+accountID+'/transfer?debitacc='+account+'&amount='+amount,
    statusCode: {
      200: function (data) {
          location.reload();
      }
  },
    error: function(data){
      console.log(data);
    }
  });
});


$(document).on('click', '#AddAccount', function(){
  event.preventDefault();
  var id = getparam('id');
  var type = prompt("Enter type of account:");
  var sortcode = parseInt(prompt("Enter sort code:"));
  var apikey = localStorage.getItem("apikey");
  $.ajax({
    type: 'POST',
    contentType: 'application/json',
    headers: { "API_TOKEN":apikey},
    url: 'http://localhost:8080/api/users/'+id+'/account?sortcode='+sortcode+'&type='+type,
    statusCode: {
      200: function (data) {
          location.reload();
      }
  },
    error: function(data){
      console.log(data);
    }
  });
});

$(document).on('click', '#getAPI', function(){
  event.preventDefault();
  var pin = parseInt(prompt("Enter your pin:"));
  var userID = localStorage.getItem("id");
  var apikey = localStorage.getItem("apikey");
  $.ajax({
    type: 'GET',
    contentType: 'application/json',
    headers: { "API_TOKEN":apikey},
    url: 'http://localhost:8080/api/users/'+userID+'/api?pin='+pin,
    success: function (data) {
      console.log(data)
      var xmlText = new XMLSerializer().serializeToString(data);
      var xmlDoc = $.parseXML( xmlText ),
      $xml = $( xmlDoc ),
      api = $xml.find("api_key").text();
      alert("your api key is: " + api);
    },
    error: function(data){
      console.log(data);
    }
  });
});

$(document).on('click', '#deleteProfile', function(){
  event.preventDefault();
  var userID = localStorage.getItem("id");
  var apikey = localStorage.getItem("apikey");
  $.ajax({
    type: 'DELETE',
    contentType: 'application/json',
    headers: { "API_TOKEN":apikey},
    url: 'http://localhost:8080/api/users/'+userID,
    success: function (data) {
      alert("Deleted!!!");
    },
    error: function(data){
      console.log(data);
    }
  });
});

$(document).on('click', '#logout', function(){
  localStorage.clear();
  window.location = 'index.html';
});

function getparam(name) {
  return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
}