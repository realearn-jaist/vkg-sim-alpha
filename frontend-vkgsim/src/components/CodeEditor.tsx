import CodeEditor from '@uiw/react-textarea-code-editor';
import { Box } from '@mui/material';

export default function MyCodeEditor({ value, handler }: { value: string, handler: (value: string) => void }) {

  return (
    <Box width={"100%"}>
      <CodeEditor
        value={value}
        onChange={(evn: { target: { value: string; }; }) => handler(evn.target.value)}
        padding={15}
        style={{
          backgroundColor: "#f5f5f5",
          fontFamily: 'ui-monospace,SFMono-Regular,SF Mono,Consolas,Liberation Mono,Menlo,monospace',
          color: "#333333", // Set your desired font color here
          fontSize: 16

        }}
        data-color-mode="dark"
      />
    </Box>

  );
}
