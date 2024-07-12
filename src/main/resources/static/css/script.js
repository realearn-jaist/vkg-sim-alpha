function editMapping() {
    document.getElementById('mappingEditor').removeAttribute('disabled');

}

function sendQuery() {
    let sparqlQuery = document.getElementById('sparqlQuery').value;
    console.log('Sending SPARQL query:', sparqlQuery);

    // Construct the request object
    let requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({query: sparqlQuery})
    };

    // Send the HTTP request
    fetch('/sendQuery', requestOptions)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            console.log('Response text:', response);
            return response.text();
        })
        .then(data => {
            console.log('Response from server:', data);
            // Handle the response data as needed
            document.getElementById('sparql-Result').innerHTML = data;
        })
        .catch(error => {
            console.error('Error sending query:', error);
            // Handle errors appropriately
        });
}

// function checkFileSize() {
//     var fileInput = document.getElementById('owlFile');
//     var fileSize = fileInput.files[0].size;
//     var maxSize = 10 * 1024 * 1024; // 10MB in bytes
//
//     if (fileSize > maxSize) {
//         alert("The file size is more than 10MB, so the list of concept names will not be shown since it will take a long time.");
//     }
// }


function enableForm() {
    // Remove disabled class to enable elements
    document.getElementById('uploadForm').classList.remove('disabled-form');
    document.getElementById('allConceptName').classList.remove('disabled-form');
}

window.onload = function () {
    const mainPage = document.getElementById('mainPage');
    if (mainPage) {
        document.getElementById('uploadForm').classList.add('disabled-form');
        document.getElementById('allConceptName').classList.add('disabled-form');
    }
}

function uploadFile() {
    // Get the form element
    let form = document.getElementById('uploadForm');
    let formData = new FormData(form);
    //check that all the fields are filled
    let owlFile = document.getElementById('owlFile');
    let propertiesFile = document.getElementById('propertiesFile');
    let driverFile = document.getElementById('driverFile');
    if (owlFile.files.length === 0 || propertiesFile.files.length === 0 || driverFile.files.length === 0) {
        alert("Please fill all the fields or make sure your files are not empty.");
        return;
    }
    console.log(FormData)
    // Send the HTTP request
    fetch('/uploadFile', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            console.log('Response from server:', data);
            findAllConceptNames();
        })
        .catch(error => {
            console.error('Error sending form data:', error);
            // Handle errors appropriately
        });
}


function findAllConceptNames() {
    console.log('Finding all concept names...')
    fetch('/findAllConceptNames')
        .then(response => response.text())
        .then(data => {
            if (typeof data === 'string') {
                try {
                    data = data.slice(1, -1).split(/\s*,\s*/);
                } catch (e) {
                    console.error('Error parsing JSON string:', e);
                    return; // Exit if parsing fails
                }
            }
            console.log('Response from server:', data);
            console.log(typeof data);
            const ulElement = document.getElementById('allConceptNameList');
            ulElement.innerHTML = ''; // Clear existing list items

            data.forEach(concept => {
                const li = document.createElement('li');
                li.textContent = concept;
                li.className = 'list-group-item'; // Bootstrap class for styling
                ulElement.appendChild(li);
            });
        })
        .catch(error => console.error('Error fetching concept names:', error));
}

function validateBaseIRI(baseIRI) {
    // Check if the value starts with "http://"
    return baseIRI.startsWith("http://");
}

function generateMapping() {
    const baseIRI = document.getElementById('baseIRI').value;
    if (validateBaseIRI(baseIRI)) {
        console.log('Generating mapping...')
        fetch(`/generateMapping?baseIRI=${baseIRI}`)
            .then(response => response.text())
            .then(data => {
                console.log('Response from server:', data);
                document.getElementById('mappingEditor').value = data; // Set textarea value with fetched content
            })
            .catch(error => console.error('Error generating mapping:', error));
    } else {
        alert("Base IRI must start with 'http://'");
    }
}

function createUserFolder() {
    const username = document.getElementById('userFolder').value;
    if (/\s/.test(username)) {
        alert('Username should not contain spaces.');
    }
    // if (username) {
    //     document.getElementById('uploadSection').classList.remove('disabled-section');
    //     document.getElementById('mappingSection').classList.remove('disabled-section');
    //     document.getElementById('querySection').classList.remove('disabled-section');
    // } else {
    //     alert('Please enter a username.');
    // }
    console.log(`Creating folder for user: ${username}`);
    fetch(`/createUserFolder?username=${username}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            enableForm();
            console.log('Response from server:', data);
        })
        .catch(error => {
            console.error('Error creating user folder:', error);
        });
}

function updateSliderValue(value) {
    document.getElementById('sliderValue').textContent = value;
}

function SimilarityMode() {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    const thresholdContainer = document.getElementById('slider-container');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            if (this.checked && this.id === 'similarityQuery') {
                thresholdContainer.style.display = 'block';
                // Uncheck the other checkbox
                document.getElementById('standardQuery').checked = false;
            } else if (this.checked && this.id === 'standardQuery') {
                thresholdContainer.style.display = 'none';
                // Uncheck the other checkbox
                document.getElementById('similarityQuery').checked = false;
            } else {
                thresholdContainer.style.display = 'none';
            }
        });
    });
}

