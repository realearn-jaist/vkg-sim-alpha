// api.ts
import axios from 'axios';

const API_BASE_URL = 'http://localhost:3000'; // Replace with your actual API base URL

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const createUserFolder = (username: string) =>
  axiosInstance.get(`/createUserFolder?username=${username}`);

export const uploadFiles = (formData: FormData) =>
  axiosInstance.post('/uploadFile', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });

export const findAllConceptNames = () =>
  axiosInstance.get('/findAllConceptNames');

export const deleteProfile = () =>
  axiosInstance.delete('/deleteProfile');

export const readMappingFileContent = () =>
  axiosInstance.get('/readMappingFileContent');

export const readBaseIRI = () =>
  axiosInstance.get('/readBaseIRI');

export const generateMapping = (baseIRI: string) =>
  axiosInstance.get(`/generateMapping?baseIRI=${baseIRI}`);

export const saveMapping = (mappingData: string) =>
  axiosInstance.post('/saveMapping', { mapping: mappingData });

export const getMappingID = () =>
  axiosInstance.get(`/getMappingID`);

export const getVisualizeMappingID = (mappingID: string) =>
  axiosInstance.get(`/getVisualizeMappingID?id=${mappingID}`);

export const sendQuery = (queryData: any) =>
  axiosInstance.post('/sendQuery', queryData);

export const similarityMeasureAllConcept = () =>
  axiosInstance.get('/similarityMeasureAllConcept');

export const getOWLFilename = () => axiosInstance.get('/getOWLFilename');

export const readConceptNameFile = () =>
  axiosInstance.get('/readConceptNameFile');

export const readSimilarityFileContent = () =>
  axiosInstance.get('/readSimilarityFileContent');

export const saveSimResultFile = (result: string) =>
  axiosInstance.post('/saveSimResultFile', { result });
