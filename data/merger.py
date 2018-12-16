import sys

def main(argv):
    output_file = './final_data.csv'
    files = [ "./intel.csv", "./amd.csv", "./nvidia.csv", "./asus.csv", "./samsung.csv", "./lenovo.csv", "./nos.csv", "./nowo.csv", "./cabovisao.csv", "./vodafone.csv", "./inesctec.csv", "./inegi.csv", "./blip.csv", "./mindera.csv", "./github.csv", "./microsoft.csv", "./mozilla.csv", "./ford.csv", "./volvo.csv", "./renault.csv" ];

    out = open(output_file, "w")

    for file_name in files:
        file = open(file_name, "r")
        for line in file:
            out.write(line)
        file.close()

    out.close()



if __name__ == '__main__':
    main(sys.argv)
