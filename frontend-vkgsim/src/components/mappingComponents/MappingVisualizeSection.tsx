import { Box, Typography, FormControl, InputLabel, MenuItem, Select, Paper } from '@mui/material';
import { getVisualizeMappingID } from '../../api';
import { useState } from 'react';

// const tmp = `
//     digraph G {
//     place [label="place"];
//     node0 [label="Place"];
//     place -> node0 [label="type"];
//     node1 [label="id"];
//     place -> node1 [label="id"];
//     node2 [label="name"];
//     place -> node2 [label="name"];
//     node3 [label="type"];
//     place -> node3 [label="type"];
//     node4 [label="location"];
//     place -> node4 [label="location"];
//     node5 [label="description"];
//     place -> node5 [label="description"];
// }
// `


const MappingVisualizeSection = ({ mappingIds }: {
    mappingIds: string[]
}) => {

    const [mappingId, setMappingId] = useState<string>('')
    const [imageSrc, setImageSrc] = useState<string>('');

    const handleChangeMappingId = async (selectedMappingId: string) => {
        setMappingId(selectedMappingId);
        if (selectedMappingId) {
            try {
                const response = await getVisualizeMappingID(selectedMappingId);
                // Assuming response.data contains the Base64 string
                setImageSrc(`data:image/png;base64,${response.data}`);
            } catch (error) {
                console.error('Error fetching the visualized mapping:', error);
            }
        }
    };

    return (
        <Box sx={{ padding: 3, bgcolor: '#ffffff', borderRadius: 2, boxShadow: 1, mb: 4 }}>
            <Typography variant="h6" sx={{ color: '#333', marginBottom: 2 }}>
                Mapping Visualize Section
            </Typography>
            <FormControl fullWidth margin="normal">
                <InputLabel id="owlFilenameLabel">Select OWL Filename</InputLabel>
                <Select
                    labelId="owlFilenameLabel"
                    value={mappingId}
                    onChange={(e) => handleChangeMappingId(e.target.value)}
                    disabled={mappingIds.length === 0}
                >
                    <MenuItem value=""><em>Select Mapping ID</em></MenuItem>
                    {mappingIds.map((id, index) => (
                        <MenuItem key={index} value={id}>{id}</MenuItem>
                    ))}
                </Select>
                <Paper
                    sx={{
                        p: 2,
                        display: 'flex',
                        flexDirection: 'column',
                        backgroundColor: 'white',
                        alignItems: 'center',
                    }}
                >
                    {imageSrc && (
                        <img src={imageSrc} alt="Mapping Visualization" style={{ maxWidth: '100%', height: 'auto' }} />
                    )}
                </Paper>
            </FormControl>
        </Box>
    );
};

export default MappingVisualizeSection;
