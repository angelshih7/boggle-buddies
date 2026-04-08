#!/bin/bash
USER=${1:-raudales-montes}
ssh -L 8080:localhost:53326 ${USER}@cs506x14.cs.wisc.edu
