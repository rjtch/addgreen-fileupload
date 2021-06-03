a=12
b=2020

cd /Users/rjtch/adorsys_workspace/addgreen-fileupload/upload-dir/stations/${b}/${a}
for f in *.csv; do
    mv -- "$f" "${f#${b}-${a}-}";
done

#
#a=12
#b=2020
#
#until [ $a -le  1 ]
#  do
#    cd /Users/rjtch/adorsys_workspace/addgreen-fileupload/upload-dir/prices/${b}/${a}
#    for f in *.csv; do
#      mv -- "$f" "${f#${b}-${a}-}";
#    done
#    if ( $a >= 09 && $a <= 01 ); then
#        ((a=0a-1))
#     else
#       ((a=a-1))
#    fi
#  done
