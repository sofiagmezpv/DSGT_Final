import {
  initializeApp
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-app.js";
import {
  getAuth,
  connectAuthEmulator,
  onAuthStateChanged,
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-auth.js";
var summer;
var winter;
var token;
// we setup the authentication, and then wire up some key events to event handlers
setupAuth();
wireGuiUpEvents();
wireUpAuthChange();

//setup authentication with local or cloud configuration. 
function setupAuth() {
  let firebaseConfig;
  if (location.hostname === "localhost") {
    firebaseConfig = {
      apiKey: "AIzaSyBoLKKR7OFL2ICE15Lc1-8czPtnbej0jWY",
      projectId: "demo-distributed-systems-kul",
    };
  } else {
    firebaseConfig = {
      // TODO: for level 2, paste your config here
    };
  }

  // signout any existing user. Removes any token still in the auth context
  const firebaseApp = initializeApp(firebaseConfig);
  const auth = getAuth(firebaseApp);
  try {
    auth.signOut();
  } catch (err) { }

  // connect to local emulator when running on localhost
  if (location.hostname === "localhost") {
    connectAuthEmulator(auth, "http://localhost:8082", { disableWarnings: true });
  }
}



function wireGuiUpEvents() {
  // Get references to the email and password inputs, and the sign in, sign out and sign up buttons
  document.addEventListener('DOMContentLoaded', function() {
      // Get references to the email and password inputs, and the sign in, sign out and sign up buttons
      var email = document.getElementById("email");
      var password = document.getElementById("password");
      var signInButton = document.getElementById("btnSignIn");
      var signUpButton = document.getElementById("btnSignUp");
      var logoutButton = document.getElementById("btnLogout");
      winter = document.getElementById("btnWinter");
      summer = document.getElementById("btnSummer");

      console.log(winter)
      console.log(summer)
      console.log(logoutButton)

      signInButton.addEventListener("click", function () {
        // Sign in the user using Firebase's signInWithEmailAndPassword method

        signInWithEmailAndPassword(getAuth(), email.value, password.value)
          .then(function () {

            console.log("signedin");
          })
          .catch(function (error) {
            // Show an error message
            console.log("error signInWithEmailAndPassword:")
            console.log(error.message);
            alert(error.message);
          });
      });

      signUpButton.addEventListener("click", function () {
        // Sign up the user using Firebase's createUserWithEmailAndPassword method

        createUserWithEmailAndPassword(getAuth(), email.value, password.value)
          .then(function () {
            console.log("created");
          })
          .catch(function (error) {
            // Show an error message
            console.log("error createUserWithEmailAndPassword:");
            console.log(error.message);
            alert(error.message);
          });
      });

      logoutButton.addEventListener("click", function () {
          try {
              var auth = getAuth();
              auth.signOut();
          } catch (err) {
              // Handle the error here if needed
              console.error(err); // Example: log the error to the console
          }
      });

    });
}

function wireUpAuthChange() {

  var auth = getAuth();
  onAuthStateChanged(auth, (user) => {
    console.log("onAuthStateChanged");
    if (user == null) {
      console.log("user is null");
      showUnAuthenticated();
      return;
    }
    if (auth == null) {
      console.log("auth is null");
      showUnAuthenticated();
      return;
    }
    if (auth.currentUser === undefined || auth.currentUser == null) {
      console.log("currentUser is undefined or null");
      showUnAuthenticated();
      return;
    }

    auth.currentUser.getIdTokenResult().then((idTokenResult) => {

      console.log("Hello " + auth.currentUser.email)

      //update GUI when user is authenticated
      showAuthenticated(auth.currentUser.email);

      console.log("Token: " + idTokenResult.token);
      console.log(summer)

winter.addEventListener("click", function () {
          console.log('winter in  idToken  clicked'); // Debugging line
          const itemId = parseInt(winter.dataset.itemId); // Assuming the "TEST" button also has a data-item-id attribute
          console.log('Item ID:', itemId); // Debugging line
          openPop(itemId)
             .then(function () {
                  console.log("opened called");
              })
             .catch(function (error) {
                  console.log("error signInWithEmailAndPassword:");
                  console.log(error.message);
                  alert(error.message);
              });

    //fetch data from server when authentication was successful.

      token = idTokenResult.token;
      fetchData(token);
    });
      summer.addEventListener("click", function () {
          console.log('btnSummer clicked'); // Debugging line
          const itemId = parseInt(summer.dataset.itemId); // Assuming the "TEST" button also has a data-item-id attribute
          console.log('Item ID:', itemId); // Debugging line
          openPop(itemId)
             .then(function () {
                  console.log("opened called");
              })
             .catch(function (error) {
                  console.log("error signInWithEmailAndPassword:");
                  console.log(error.message);
                  alert(error.message);
              });

    //fetch data from server when authentication was successful.

      token = idTokenResult.token;
      fetchData(token);
    });

  });
});
}

function openPop(itemId) {
    fetch(`/add_to_cart?id=${itemId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ /* Item details */ })
    })
    .then(response => response.text())
    .then(data => console.log(data))
    .catch(error => console.error('Error adding item to cart:', error));

    const popDialog = document.getElementById("popupDialog");
    popDialog.style.visibility = popDialog.style.visibility === "visible" ? "hidden" : "visible";
    setTimeout(closePop, 1000);

}

// Define closePop function
function closePop() {
    const popDialog = document.getElementById("popupDialog");
    popDialog.style.visibility = "hidden";
}

function fetchData(token) {
  getHello(token);
  whoami(token);
}
function showAuthenticated(username) {

  document.getElementById("namediv").innerHTML = "Hello " + username;
  document.getElementById("logindiv").style.display = "none";
  document.getElementById("contentdiv").style.display = "block";
}

function showUnAuthenticated() {
  document.getElementById("namediv").innerHTML = "";
  document.getElementById("email").value = "";
  document.getElementById("password").value = "";
  document.getElementById("logindiv").style.display = "block";
  document.getElementById("contentdiv").style.display = "none";
}

function addContent(text) {
  document.getElementById("contentdiv").innerHTML += (text + "<br/>");
}

// calling /api/hello on the rest service to illustrate text based data retrieval
function getHello(token) {

  fetch('/api/hello', {
    headers: { Authorization: 'Bearer {token}' }
  })
    .then((response) => {
      return response.text();
    })
    .then((data) => {

      console.log(data);
      addContent(data);
    })
    .catch(function (error) {
      console.log(error);
    });


}


// calling /api/whoami on the rest service to illustrate JSON based data retrieval
function whoami(token) {

  fetch('/api/whoami', {
    headers: { Authorization: 'Bearer ' + token }
  })
    .then((response) => {
      return response.json();
    })
    .then((data) => {
      console.log(data.email + data.role);
      addContent("Whoami at rest service: " + data.email + " - " + data.role);

    })
    .catch(function (error) {
      console.log(error);
    });



}

