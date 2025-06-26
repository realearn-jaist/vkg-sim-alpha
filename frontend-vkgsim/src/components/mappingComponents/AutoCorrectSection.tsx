import { Button, Box, Typography, Paper } from '@mui/material';

const AutoCorrectSection = ({ mapping, setMapping, conceptNames }: {
    mapping: string, setMapping: (value: string) => void, conceptNames: string[]
}) => {
    const handleAutoCorrect = () => {
        let updatedMapping = mapping; // Start with the current mapping
        conceptNames.forEach((concept) => {
            const regex = new RegExp(`(a\\s+.*?)(${concept})(.*?\\s*[;\\.])`, 'gi');
            updatedMapping = updatedMapping.replace(regex, (_match, p1, _p2, p3) => {
                return `${p1}${concept}${p3}`; // Replace the matched concept with the correct case
            });
        });
        setMapping(updatedMapping); // Update state with the corrected mapping
    };
    

    return (
        <Box mb={4}>
            <Paper sx={{ overflow: "auto", p: 2, bgcolor: '#ffffff', borderRadius: 2, boxShadow: 3 }}>
                <Typography variant="h6" mb={2} sx={{ textAlign: 'center' }}>Auto Correction</Typography>
                <Button
                    variant="contained"
                    sx={{ bgcolor: '#0097a7', color: 'white', '&:hover': { bgcolor: '#00838f' } }}
                    onClick={handleAutoCorrect}
                >
                    Auto Correct Mapping
                </Button>
            </Paper>
        </Box>
    );
};

export default AutoCorrectSection;
