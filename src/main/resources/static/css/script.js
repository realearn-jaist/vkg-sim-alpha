window.onload = function () {
    /**
     * Check if the current page is the main page or the mapping page
     * and call the appropriate function based on the page.
     * */
    const mainPage = document.getElementById('mainPage');
    const mappingPage = document.getElementById('mappingPage');
    if (mainPage) {
        console.log('Main page loaded');
        document.getElementById('uploadForm').classList.add('disabled-form');
        document.getElementById('allConceptName').classList.add('disabled-form');
    }
    if (mappingPage) {
        console.log('Mapping page loaded');
        readMapping();
    }
}

function createUserFolder() {
    /**
     * Create a folder for the user based on the username entered in the form.
     * */
    const username = document.getElementById('userFolder').value;
    if (/\s/.test(username)) {
        alert('Username should not contain spaces.');
    }
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

function uploadFile() {
    /**
     * Upload the files to the server.
     * */

    let form = document.getElementById('uploadForm');
    let formData = new FormData(form);
    let owlFile = document.getElementById('owlFile');
    let mappingFile = document.getElementById('mappingFile');
    let propertiesFile = document.getElementById('propertiesFile');
    let driverFile = document.getElementById('driverFile');
    if (owlFile.files.length === 0 || propertiesFile.files.length === 0 || driverFile.files.length === 0 || mappingFile.files.length === 0) {
        alert("Please fill all the fields or make sure your files are not empty.");
        return;
    }
    console.log(FormData)
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
            SimilarityMeasureAllConcept();
        })
        .catch(error => {
            console.error('Error sending form data:', error);
            // Handle errors appropriately
        });
}


function showInfo() {
    /**
     * Show the info popup when the user clicks the info button.
     * */
    var popup = document.getElementById("infoPopup");
    console.log(popup)
    popup.classList.toggle("show");
}

window.onclick = function (event) {
    /**
     * Close the info popup when the user clicks outside of the popup.
     * */
    var popup = document.getElementById("infoPopup");
    var infoButton = document.querySelector(".btn-info"); // Adjust selector based on your button class or ID

    if (popup && infoButton) {
        if (event.target !== popup && !popup.contains(event.target) && event.target !== infoButton) {
            popup.classList.remove("show");
        }
    }
}


function findAllConceptNames() {
    /**
     * Fetch all concept names from the server and display them in a list.
     * */
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
    /**
     * Validate the base IRI entered by the user.
     */
    return baseIRI.startsWith("http://");
}


function readMapping() {
    /**
     * Read the mapping file content from the server and display it in the textarea.
     */
    fetch('/readMappingFileContent')
        .then(response => response.text())
        .then(data => {
            console.log('Response from server:', data);
            document.getElementById('mappingEditor').value = data;
        })
        .catch(error => console.error('Error reading mapping:', error));

}

function generateMapping() {
    /**
     * Generate the mapping based on the base IRI entered by the user.
     */
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


function editMapping() {
    /**
     * Enable the mapping editor textarea for editing.
     */
    document.getElementById('mappingEditor').removeAttribute('disabled');
}

function saveMapping() {
    /**
     * Save the edited mapping to the server
     */
    var mappingData = document.getElementById('mappingEditor').value;
    var requestData = {
        mapping: mappingData
    };

    fetch('/saveMapping', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            console.log('Mapping edited successfully:', data);
        })
        .catch(error => {
            console.error('Error editing mapping:', error);
        });
}

function sendQuery() {
    /**
     * Send the SPARQL query to the server and display the result.
     */
    let sparqlQuery = document.getElementById('sparqlQuery').value;
    let owlFileType = document.getElementById('owlFilenameDropdown').value;
    let checkedValue;

    const standardQueryCheckbox = document.querySelector('#standardQuery');
    const similarityQueryCheckbox = document.querySelector('#similarityQuery');

    if (standardQueryCheckbox.checked) {
      checkedValue = standardQueryCheckbox.value;
    } else {
      checkedValue = similarityQueryCheckbox.value;
    }

    console.log('Sending SPARQL query:', sparqlQuery);
    console.log('OWL File Type:', owlFileType);
    console.log('Query Type:', checkedValue);

    let requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({query: sparqlQuery, owlFileType: owlFileType, queryType: checkedValue})
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
    /**
     * Enable the form elements after the user folder is created.
     */
    // Remove disabled class to enable elements
    document.getElementById('uploadForm').classList.remove('disabled-form');
    document.getElementById('allConceptName').classList.remove('disabled-form');
}

function SimilarityMode() {
    /**
     * Enable the similarity mode based on the checkbox selected.
     */
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    const thresholdContainer = document.getElementById('slider-container');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            if (this.checked && this.id === 'similarityQuery') {
                thresholdContainer.style.display = 'block';
                document.getElementById('standardQuery').checked = false;
                readSimilarityFileContent();
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

function updateSliderValue(value) {
    /**
     * Update the slider value based on the slider position.
     */
    document.getElementById('sliderValue').textContent = value;
    readSimilarityFileContent(value);
}

function readSimilarityFileContent(threshold) {
    /**
     * Read the similarity file content from the server and display it in the textarea.
     */
    fetch('/readSimilarityFileContent')
        .then(response => response.text())
        .then(data => {
            // Split the data into lines
            let lines = data.split('\n');
            let result = '';
            // Process each line

            threshold = document.getElementById('similarityThreshold').value;

            lines.forEach(line => {
                if (line.trim()) {  // Check for non-empty line
                    let parts = line.split(',');
                    if (parts.length === 3) {
                        let concept1 = parts[0];
                        let concept2 = parts[1];
                        let similarity = parts[2];
                        // Check if similarity is above threshold
                        if (parseFloat(similarity) >= parseFloat(threshold)) {
                            result += `Concept 1: ${concept1}, Concept 2: ${concept2}, Similarity: ${similarity}\n`;
                        }
                    }
                }
            });
//            console.log(result);  // Log the formatted result
            saveSimResultFile(result)
            // Update the textarea with the formatted result
            document.getElementById('sparql-Result').value = result;
        })
        .catch(error => console.error('Error reading Similarity file:', error));
}

function saveSimResultFile(result) {

    fetch('/saveSimResultFile', {
        method: 'POST', // Assuming you want to send a POST request
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({result: result}),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .catch(error => console.error('Error saving concept names:', error));

}


function SimilarityMeasureAllConcept() {
    /**
     * Generate the similarity measure query for all concepts based on the threshold entered by the user.
     */
    fetch('/similarityMeasureAllConcept', {
        method: 'GET', // Changed to GET method
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => response.text())
        .then(data => {
            console.log('Response from server:', data);
        })
        .catch(error => console.error('Error generating similarity query:', error));
}


document.addEventListener("DOMContentLoaded", function () {
    const queryPage = document.getElementById('queryPage');
    const mappingPage = document.getElementById('mappingPage');
    if (queryPage) {
        getOWLFilename();
        readConceptNameFile();
    }
    if (mappingPage) {
        readConceptNameFile();
    }
});

function getOWLFilename() {
    /**
     * Get the OWL filenames (normal + bootstrap) from the server and populate the dropdown.
     */
    fetch('/getOWLFilename')
        .then(response => response.json())
        .then(data => {
            const dropdown = document.getElementById('owlFilenameDropdown');
            dropdown.innerHTML = ''; // Clear existing options

            data.forEach(filename => {
                const option = document.createElement('option');
                option.value = filename;
                option.textContent = filename;
                dropdown.appendChild(option);
            });
        })
        .catch(error => console.error('Error getting OWL filenames:', error));
}

function readConceptNameFile() {
    fetch('/readConceptNameFile')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            document.getElementById('allConceptName').value = data;
        })
        .catch(error => console.error('Error reading concept names:', error));

}