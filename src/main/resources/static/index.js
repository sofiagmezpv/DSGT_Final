import { initializeApp } from "https://www.gstatic.com/firebasejs/9.9.4/firebase-app.js";
import {
  getAuth,
  connectAuthEmulator,
  onAuthStateChanged,
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-auth.js";
import { getFirestore, collection, addDoc , getDocs } from 'https://www.gstatic.com/firebasejs/9.9.4/firebase-firestore.js';

let db;
var token;
var cart;
var managerGetAllOrders;
var managerAllCustomers;

// Setup authentication and wire up event handlers
setupAuth();
wireGuiUpEvents();
wireUpAuthChange();

//pop up buttons
const closeCart = document.getElementById("closeCartButton");
const closeUsers = document.getElementById("closeUsersButton")
const closeOrders = document.getElementById("closeOrdersButton")

//pop up dialogs
const cartpop = document.getElementById("cartPopup");
const userspop = document.getElementById("usersPopup");
const orderspop = document.getElementById("ordersPopup");


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

  const firebaseApp = initializeApp(firebaseConfig);
  const auth = getAuth(firebaseApp);
  db = getFirestore(firebaseApp);

  try {
    auth.signOut();
  } catch (err) {
    console.error('Error signing out:', err);
  }

  if (location.hostname === "localhost") {
    connectAuthEmulator(auth, "http://localhost:8082", { disableWarnings: true });
  }
}

function wireGuiUpEvents() {
  document.addEventListener('DOMContentLoaded', function () {
    var email = document.getElementById("email");
    var password = document.getElementById("password");
    var signInButton = document.getElementById("btnSignIn");
    var signUpButton = document.getElementById("btnSignUp");
    var logoutButton = document.getElementById("btnLogout");
    cart = document.getElementById("cartButton");
    managerGetAllOrders = document.getElementById("managerAllOrdersButton");
    managerAllCustomers = document.getElementById("managerAllCustomersButton");

    signInButton.addEventListener("click", function () {
      signInWithEmailAndPassword(getAuth(), email.value, password.value)
        .then(function () {
          console.log("signed in");
        })
        .catch(function (error) {
          console.error("error signInWithEmailAndPassword:", error.message);
          alert(error.message);
        });
    });

    signUpButton.addEventListener("click", function () {
      const auth = getAuth();
      createUserWithEmailAndPassword(auth, email.value, password.value)
     .then(cred => {
          console.log('into signup db methods');
//          addUserCred(cred);
        })
     .catch(error => { // Catch any errors that occur during the Firestore operation
          console.error("Error adding user to Firestore:", error);
          alert("Failed to add user to Firestore. Please try again.");
        });
    });



    logoutButton.addEventListener("click", function () {
      try {
        const auth = getAuth();
        auth.signOut();
      } catch (err) {
        console.error('Error signing out:', err);
      }
    });
  });
}

function wireUpAuthChange() {
  const auth = getAuth();
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

    auth.currentUser.getIdTokenResult(auth.currentUser.getIdToken()).then((idTokenResult) => {
      showAuthenticated(auth.currentUser.email);
      console.log("Token:", idTokenResult.token);
      fetchPackages(idTokenResult.token);

      addUserCred(idTokenResult.token, auth.currentUser.uid, auth.currentUser.email);
      console.log(auth.currentUser.uid);

      cart.addEventListener("click", function () {
        console.log('cart open clicked');
        openCartPopup()
          .then(function () {
            console.log("opened cart");
          })
          .catch(function (error) {
            console.error("error opening cart:", error.message);
            alert(error.message);
          });

        token = idTokenResult.token;
        fetchData(token);
      });

      if (idTokenResult.claims.role === 'admin') {
        console.log('User has admin role');
        managerGetAllOrders.style.visibility = "visible";
        managerAllCustomers.style.visibility = "visible";

        managerGetAllOrders.addEventListener("click", function () {
          console.log('manager get all orders clicked');
          managerAllOrdersPopUp(idTokenResult.token)
            .then(function () {
              console.log("manager cart");
            })
            .catch(function (error) {
              console.error("error opening cart:", error.message);
              alert(error.message);
            });
          token = idTokenResult.token;
          fetchData(token);
        });

        managerAllCustomers.addEventListener("click", function () {
          console.log('manager get all customers clicked');
          managerAllCustomersPopUp(idTokenResult.token)
            .then(function () {
              console.log("manager cart");
            })
            .catch(function (error) {
              console.error("error opening cart:", error.message);
              alert(error.message);
            });
          token = idTokenResult.token;
          fetchData(token);
        });

      } else {
        console.log('User does not have admin role');
        managerGetAllOrders.style.visibility = "hidden";
        managerAllCustomers.style.visibility = "hidden";
      }
    }).catch((error) => {
      console.error('Error getting ID token result:', error);
    });
  });
}

function openPop(packageId) {
    const auth = getAuth(); // Assuming this function gets the authentication object
    let uidString = ""; // Initialize username variable
    console.log(auth.currentUser.uid)
    // Check if the user is authenticated
    if (auth.currentUser) {
        uidString = auth.currentUser.uid; // Retrieve username from currentUser's email
    } else {
        console.log("User not authenticated");
        return; // Exit the function if user is not authenticated
    }

    fetch(`/add_to_cart?id=${packageId}&uid=${uidString}`, { // Include username in the fetch URL
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

function addUserCred(token, uid, email){
    console.log(token)
    fetch(`/api/addUserCred?uid=${uid}&username=${email}`, {
        method: 'POST',
        headers: { Authorization: 'Bearer ' + token }
    })
    .then(() => {
        console.log('added user');
    })
     .catch(error => {
        console.error('Error removing package from cart:', error);
    });

}

// Define closePop function
function closePop() {
    console.log("closing pop up")
    const popDialog = document.getElementById("popupDialog");
    popDialog.style.visibility = "hidden";
}


// Add event listener to the cart items container
const cartItemsContainer = document.getElementById("cartItems");


function openCartPopup() {
    const auth = getAuth();

    // Check if the user is authenticated
    if (auth.currentUser) {
        const uidString = auth.currentUser.uid;

        // Fetch user's packages from the server
        fetch(`/user/packages/${uidString}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(packages => {
            // Clear previous items
            cartItemsContainer.innerHTML = "";

            // Iterate over the packages and dynamically populate the cart section
            packages.forEach(pkg => {
                const packageElement = document.createElement("div");
                packageElement.innerHTML = `
                    <p>${pkg.name} - $${pkg.price.toFixed(2)}</p>
                    <button data-package-id="${pkg.id}" type="button" class="btn btn-danger remove-btn">Remove</button>
                `;
                // Attach event listener to the Remove button
                packageElement.querySelector('.remove-btn').addEventListener('click', () => {
                    removePackageFromCart(pkg.id);
                });

                cartItemsContainer.appendChild(packageElement);

                closeCart.addEventListener('click' , () => {
                    closeCartPop();
                });


            });
        })
        .catch(error => {
            console.error('Error fetching user packages:', error);
        });
    } else {
        console.log("User not authenticated");
    }

    cartpop.style.visibility = "visible"
}


function removePackageFromCart(packageId) {
    const auth = getAuth();
    let uidString = "";

    // Check if the user is authenticated
    if (auth.currentUser) {
        uidString = auth.currentUser.uid; // Retrieve username from currentUser's email
    } else {
        console.log("User not authenticated");
        return; // Exit the function if user is not authenticated
    }

    // Send a DELETE request to the server to remove the package
    fetch(`/remove_from_cart?id=${packageId}&uid=${uidString}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(() => {
        console.log(`Package with ID ${packageId} removed from cart.`);
        // Reload the cart content directly here
        const cartItemsContainer = document.getElementById("cartItems");
        cartItemsContainer.innerHTML = ""; // Clear previous items

        openCartPopup();
    })
     .catch(error => {
        console.error('Error removing package from cart:', error);
    });
}


// Example function to fetch the current cart items
function fetchCurrentCartItems() {
    const auth = getAuth(); // Assuming this function gets the authentication object
    let username = ""; // Initialize username variable
    console.log("fetch current cart items");
    // Check if the user is authenticated
    if (auth.currentUser) {
        username = auth.currentUser.email; // Retrieve username from currentUser's email
    } else {
        console.log("User not authenticated");
        // Handle the case where the user is not authenticated
        // You may display a message or redirect to a login page
        return;
    }

    // Fetch user's packages from the server
    fetch(`/user/packages/${username}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
   .then(response => response.json())
   .then(packages => {
        // Populate the cart section with the fetched packages
        const cartItemsContainer = document.getElementById("cartItems");
        cartItemsContainer.innerHTML = "";
        packages.forEach(pkg => {
            const packageElement = document.createElement("div");
            packageElement.innerHTML = `
                <p>${pkg.name} - $${pkg.price.toFixed(2)}</p>
                <button data-package-id="${pkg.id}" type="button" class="btn btn-danger remove-btn">Remove</button>
            `;
            packageElement.querySelector('.remove-btn').addEventListener('click', () => {
                removePackageFromCart(pkg.id); // Pass both the document ID and the package ID
            });
            cartItemsContainer.appendChild(packageElement);
        });
    })
   .catch(error => {
        console.error('Error fetching user packages:', error);
    });
}

function closeCartPop() {
    cartpop.style.visibility = "hidden";
}

function closeUsersPop(){
   userspop.style.visibility = "hidden";
}

function closeOrdersPop(){
    orderspop.style.visibility = "hidden";
}

function buy() {
    const popDialog4 =
        document.getElementById(
            "paymentPopup"
        );
    popDialog4.style.visibility =
        popDialog4.style.visibility ===
        "visible"
            ? "hidden"
            : "visible";
}


function closePayment() {
    const popDialog5 = document.getElementById("paymentPopup");
    popDialog5.style.visibility = "hidden";
}


function donePurchase() {
    const paymentPopup = document.getElementById("paymentPopup");
    const cartPopup = document.getElementById("cartPopup");
    const confirmationPopup = document.getElementById("confirmationPopup");

    paymentPopup.style.visibility = "hidden";
    cartPopup.style.visibility = "hidden";
    confirmationPopup.style.visibility = "visible";

    closeConfirmation(); // Call closeConfirmation function after showing confirmation popup
}


function closeConfirmation() {
    const popDialog6 = document.getElementById("confirmationPopup");
    popDialog6.style.visibility = "hidden";
    //class="bg-light box-shadow mx-auto"
    //style="width: 80%; height: 300px;
    //class="bg-dark box-shadow mx-auto"
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


function fetchPackages(token) {
  fetch('/packages', {
    headers: { Authorization: 'Bearer ' + token }
  })
    .then((response) => response.json())
    .then((packages) => {
      console.log(packages);
      displaypackages(packages)
    })
    .catch((error) => {
      console.error('Error fetching packages:', error);
    });
}


function displaypackages(packages) {
  const packagesDiv = document.getElementById('packagesDiv');
    const template = document.getElementById('packageTemplate').content;
    const templateDark = document.getElementById('packageTemplateDark').content;
    packagesDiv.innerHTML = ''; // Clear existing content

    let int = 0;
    packages.forEach(pkg => {
      let packageElement;
      if (int % 2 === 1) {
        packageElement = template.cloneNode(true);
      } else {
        packageElement = templateDark.cloneNode(true);
      }

      packageElement.querySelector('.card-name').textContent = pkg.name;
      packageElement.querySelector('.card-price').textContent = `$${pkg.price.toFixed(2)}`;
      packageElement.querySelector('.card-description').textContent = pkg.description;
      packageElement.querySelector('.card-items').textContent = pkg.itemNames;
      packageElement.querySelector('.card-button').dataset.packageId = pkg.id;

      const button = packageElement.querySelector('.card-button');
      button.addEventListener("click", function () {
        const packageId = this.dataset.packageId;
        console.log('Package ID:', packageId); // Debugging line
        openPop(packageId)
        .then(function () {
          console.log("Popup opened successfully");
        })
        .catch(function (error) {
          console.log("Error opening popup:", error.message);
          alert(error.message);
        });
      });
      int++;
      packagesDiv.appendChild(packageElement);
    });
}

function managerAllOrdersPopUp(token) {
            console.log('Token in manager all orders')
            const auth = getAuth();
            // Check if the user is authenticated
            if (auth.currentUser) {
                // Fetch user's packages from the server
                fetch(`/api/getAllOrders`, {
                    method: 'GET',
                    headers: { Authorization: 'Bearer ' + token }
                })
                .then(response => response.json())
                .then(orders => {
                    const ordersItemsContainer = document.getElementById("ordersItems");
                    ordersItemsContainer.innerHTML = "";

                            orders.forEach(orderI => {
                            const ordersElement = document.createElement("div");
                            ordersElement.innerHTML = `
                                <p>Order ID: ${orderI.id}</p>
                                <p>Packages: ${orderI.packages.join(", ")}</p>
                                <p>Price: ${orderI.price}</p>
                                `;
                            ordersItemsContainer.appendChild(ordersElement);
                            });
                })
                .catch(error => {
                    console.error('Error fetching user packages:', error);
                });
            } else {
                console.log("User not authenticated");
            }

            // Show the cart popup
            const popDialogOrders = document.getElementById("ordersPopup");
            popDialogOrders.style.visibility =
                popDialogOrders.style.visibility === "visible"
                    ? "hidden"
                    : "visible";
            closeOrders.addEventListener('click' , () => {
                closeOrdersPop();
            });
}


function managerAllCustomersPopUp(token){

    // Fetch user's packages from the server
    fetch(`/api/getAllCustomers`, {
        method: 'GET',
        headers: { Authorization: 'Bearer ' + token }
    })
   .then(response => response.json())
   .then(users => {
        const usersItemsContainer = document.getElementById("usersItems");
        usersItemsContainer.innerHTML = "";

        users.forEach(userI => {
            const usersElement = document.createElement("div");
            usersElement.innerHTML = `
                <p>${userI.email}</p>
            `;
            usersItemsContainer.appendChild(usersElement);
        });
   })
   .catch(error => {
        console.error('Error fetching user packages:', error);
   });
   const popDialogOrders = document.getElementById("usersPopup");
               popDialogOrders.style.visibility =
                   popDialogOrders.style.visibility === "visible"
                       ? "hidden"
                       : "visible";

   closeUsers.addEventListener('click' , () => {
        closeUsersPop();
   });
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

