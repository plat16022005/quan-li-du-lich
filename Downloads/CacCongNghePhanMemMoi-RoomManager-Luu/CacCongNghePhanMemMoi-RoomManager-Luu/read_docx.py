import docx
import sys

def read_docx(file_path):
    try:
        doc = docx.Document(file_path)
        with open('output_docx.txt', 'w', encoding='utf-8') as f:
            for i, para in enumerate(doc.paragraphs):
                f.write(f"[{i}] {para.text}\n")
    except Exception as e:
        print("Error:", e)

if __name__ == "__main__":
    read_docx('BugReport_PhongTro.docx')
